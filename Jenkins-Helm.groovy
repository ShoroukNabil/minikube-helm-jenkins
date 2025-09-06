pipeline {
    agent any

    environment {
        HELM_RELEASE   = "my-app"
        HELM_NAMESPACE = "devops"
        //HELM_CHART     = "."
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                git branch: 'main', url: 'https://github.com/ShoroukNabil/minikube-helm-jenkins.git'
            }
        }

        stage('Helm Deploy') {
            steps {
                script {
                    def GIT_COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    echo "Deploying ${HELM_RELEASE}:${GIT_COMMIT_ID} to namespace ${HELM_NAMESPACE}"

                    sh """
                    helm upgrade --install ${HELM_RELEASE} . \
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
