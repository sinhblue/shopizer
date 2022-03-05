pipeline {
    agent {
        docker {
            image 'maven:3.8.4-ibmjava-8-alpine'
            args '-u root'
        }
    }
    stages {
        stage('build') {
            steps {
                checkout scm
                sh 'echo "shopizer build"'
                sh '''
                    whoami
                    pwd
                    ls -lah
                    ./mvnw clean install
                    cd sm-shop
                '''
            }
        }
    }
}
