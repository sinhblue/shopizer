pipeline {
    agent any
    tools {
        maven "maven-3.8.4"
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
                    ls -lah
                '''
            }
        }
    }
}
