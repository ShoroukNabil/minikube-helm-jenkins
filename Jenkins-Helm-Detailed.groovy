pipeline {
    agent any

    environment {
        HELM_RELEASE   = "myapp"
        HELM_NAMESPACE = "devops"
    }

    stages {
        stage('Pre-Flight Checks') {
            steps {
                script {
                    echo "🔍 Checking cluster connectivity..."
                    sh """
                    echo ">> Checking Minikube status"
                    minikube status || (echo "❌ Minikube is not running!" && exit 1)

                    echo ">> Checking kubectl cluster-info"
                    kubectl cluster-info || (echo "❌ Cannot reach cluster!" && exit 1)

                    echo ">> Ensuring namespace exists"
                    kubectl get ns ${HELM_NAMESPACE} || kubectl create ns ${HELM_NAMESPACE}
                    """
                }
            }
        }

        stage('Lint Helm Chart') {
            steps {
                sh 'helm lint .'
            }
        }

        stage('Dry Run Deployment') {
            steps {
                echo "🔧 Helm dry-run to preview manifests"
                sh """
                helm upgrade --install ${HELM_RELEASE} . \
                    --namespace ${HELM_NAMESPACE} \
                    --create-namespace \
                    --dry-run --debug
                """
            }
        }

        stage('Helm Deploy') {
            steps {
                script {
                    def GIT_COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    echo "🚀 Deploying ${HELM_RELEASE}:${GIT_COMMIT_ID} to namespace ${HELM_NAMESPACE}"

                    sh """
                    helm upgrade --install ${HELM_RELEASE} . \
                        --namespace ${HELM_NAMESPACE} \
                        --create-namespace \
                        --set image.tag=${GIT_COMMIT_ID}
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                echo "🔎 Verifying pods and waiting for readiness..."
                sh """
                kubectl rollout status deployment/${HELM_RELEASE} -n ${HELM_NAMESPACE} --timeout=120s
                kubectl get all -n ${HELM_NAMESPACE}
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment succeeded! Access your app with: minikube service list"
        }
        failure {
            echo "⚠️ Deployment failed. Attempting rollback..."
            sh "helm rollback ${HELM_RELEASE} || echo 'No previous release to roll back to'"
        }
    }
}
