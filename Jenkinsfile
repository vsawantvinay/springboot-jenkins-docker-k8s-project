pipeline {
    agent any

    stages {
        stage('git-checkout') {
            steps {
                git branch: 'main', credentialsId: 'git-cred', url: 'https://github.com/vsawantvinay/springboot-jenkins-docker-k8s-project.git'
            }
        }

        stage('mvn build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('docker images') {
            steps {
                sh 'docker build -t myimage .'
            }
        }

         stage('docker push') {
            steps {
                script {
                withDockerRegistry(credentialsId: 'docker-cred') {
                sh 'docker tag myimage vsawantvinay/practiceproject:v1'
                sh ' docker push vsawantvinay/practiceproject:v1'
                    }
                }
            }
        }
            
            stage('deploy to k8s') {
            steps {
                sh 'kubectl apply -f k8s/deployment.yml'
            }
        }

    }
}
