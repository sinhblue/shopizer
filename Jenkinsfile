pipeline {
    agent any
    stages {
        stage('Shopizer Test') {
            agent {
                docker {
                    image 'shopizerecomm/ci:java11'
                }
            }
            steps {
                sh 'echo "shopizer build and test"'
                sh '''
                    pwd
                    id $(whoami)
                    cat /home/shopizer/tools/shopizer.sh
                    set -x
                    /home/shopizer/tools/shopizer.sh tests
                '''
            }
        }
        stage('Build image') {
            steps {
                sh '''
                    echo "cd sm-shop && docker build . -t $DOCKER_ID/shopizer:$BUILD_NUMBER"
                '''
            }
        }
        stage('Push image') {
            steps {
                sh '''
                    echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_ID" --password-stdin
                    docker push $DOCKER_ID/shopizer:$BUILD_NUMBER
                '''
            }
        }
    }
}