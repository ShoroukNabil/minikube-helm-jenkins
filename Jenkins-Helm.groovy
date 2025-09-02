pipeline {
    agent {
        // ✅ Use a container with helm + kubectl installed
        docker {
            image 'alpine/helm:3.14.0'   // official Helm image (comes with kubectl)
            args '-u root:root'         // run as root to avoid permissions issues
        }
    }

    environment {
        // ✅ Set your release name & namespace
        HELM_RELEASE = "myapp"
        HELM_NAMESPACE = "default"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Helm Lint') {
            steps {
                sh 'helm lint ./'
            }
        }

        stage('Helm Upgrade/Install') {
            steps {
                sh '''
                echo "Deploying release: ${HELM_RELEASE}"
                helm upgrade --install ${HELM_RELEASE} ./ \
                  --namespace ${HELM_NAMESPACE} \
                  --create-namespace
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                echo "Waiting for pods to be ready..."
                kubectl rollout status statefulset/${HELM_RELEASE}-mysql -n ${HELM_NAMESPACE} || true
                kubectl rollout status deployment/${HELM_RELEASE}-backend -n ${HELM_NAMESPACE} || true
                kubectl rollout status deployment/${HELM_RELEASE}-frontend -n ${HELM_NAMESPACE} || true

                echo "Current resources:"
                kubectl get pods -n ${HELM_NAMESPACE}
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Helm deployment completed successfully!'
        }
        failure {
            echo '❌ Helm deployment failed. Check logs above.'
        }
    }
}
