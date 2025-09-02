pipeline {
    agent any

    environment {
        HELM_RELEASE   = "my-app"
        HELM_NAMESPACE = "dev"
        HELM_CHART     = "./helm/my-app"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                git branch: 'main', url: 'https://github.com/your/repo.git'
            }
        }

        stage('Helm Deploy') {
            steps {
                script {
                    def GIT_COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    echo "Deploying ${HELM_RELEASE}:${GIT_COMMIT_ID} to namespace ${HELM_NAMESPACE}"

                    sh """
                    kubectl config use-context minikube
                    helm upgrade --install ${HELM_RELEASE} ${HELM_CHART} \
                        --namespace ${HELM_NAMESPACE} --create-namespace \
                        --set image.tag=${GIT_COMMIT_ID}
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh "kubectl get pods -n ${HELM_NAMESPACE}"
            }
        }
    }

    post {
        success {
            echo "Deployment succeeded! Check your app with 'minikube service list'."
        }
        failure {
            echo "Deployment failed. Check Jenkins logs for details."
        }
    }
}
