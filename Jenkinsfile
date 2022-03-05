pipeline {
    agent any
    tools {
        maven "maven-3.8.4"
    }
    stages {
        stage('Build binary') {
            steps {
                checkout scm
                sh 'echo "shopizer build"'
                sh '''
                    whoami
                    pwd
                    ls -lah
                    mvn clean install
                    cd sm-shop
                    ls -lah
                '''
            }
        }
        stage('Build docker image') {
            steps {
                withCredentials([string(credentialsId: 'DOCKER_PASSWORD', variable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        DOCKER_ID=sinhblue
                        cd sm-shop && docker build . -t $DOCKER_ID/shopizer:$BUILD_NUMBER
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_ID" --password-stdin
                        docker push $DOCKER_ID/shopizer:$BUILD_NUMBER
                    '''
                }
            }
        }
    }
}
