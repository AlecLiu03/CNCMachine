pipeline {
    agent { docker { image 'maven:3.9.0-eclipse-temurin-11' } }
    stages {
        stage('build') {
            steps {
                sh 'mvn --version'
            }
        }
    }
    post {
        success {
            mail to: 'alec.liu092@gmail.com',
                 subject: 'Successful Pipeline: ${currentBuild.fullDisplayName}',
                 body: 'Successful build!'
        }
        failure {
            mail to: 'alec.liu092@gmail.com',
                 subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                 body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}