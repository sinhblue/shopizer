pipeline {
    agent any
    stages {
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
                    echo "hoilamgi@287" | docker login --username sinhblue --password-stdin
                    docker push $DOCKER_ID/shopizer:$BUILD_NUMBER
                '''
            }
        }
    }
}