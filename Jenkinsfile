pipeline {
    agent any
    
    environment {
        IMAGE = 'my-devsecops-img'
        container_name = 'myproject'
        TRIVY_CACHE_DIR = '.trivycache' 
            }

    stages {
        stage('Git-Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/vsawantvinay/springboot-jenkins-docker-k8s-project.git'
            }
        }

        stage('Set Image Tag') {
            steps {
                script {
                    env.Image_Tag = env.GIT_COMMIT.take(7)

                    echo "Image_Tag = ${env.Image_Tag}"
                }
            }
        }
        
        
            stage('Resolve Maven Dependencies') { 
                steps { 
                    sh 'mvn -B dependency:resolve '
                    }
                } 
            
            stage('Trivy FS Scan') {
                steps {
                sh '''
                mkdir -p reports
                trivy fs . --severity HIGH,CRITICAL --exit-code 0 --format sarif -o reports/trivy-fs.sarif
                '''
            }
        }
        
        stage('mvn build') {
            steps {
                sh 'mvn clean verify'
            }
        }
        
        stage('mvn test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('sonar-scan') {
            steps {
                withSonarQubeEnv('sonar-server') {
               sh "/usr/bin/mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=myproject -Dsonar.projectName='myproject'"
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token'
            }
        }
        
        stage('docker build') {
            steps {
                sh 'docker build -t ${IMAGE} .'
            }
        }
        
        stage('Trivy Image Scan') {
            steps {
                sh '''
                trivy image --severity HIGH,CRITICAL --exit-code 0 --format sarif -o reports/trivy-image.sarif ${IMAGE}
                '''
            }
        }
        
        stage('push docker image') {
            steps {
                script {
                withDockerRegistry(credentialsId: 'docker-cred') {
                sh '''
                docker tag ${IMAGE} vsawantvinay/${IMAGE}:${Image_Tag}
                docker push vsawantvinay/${IMAGE}:${Image_Tag}
                '''
                    }
                }
            }
        }
        
        stage('remove container') {
            steps {
                sh '''
                docker stop myproject || true 
                docker rm myproject || true 
                '''

            }
        }
        
        stage('create container') {
            steps {
                sh 'docker run -itd -p 9090:9090 --name ${container_name} vsawantvinay/${IMAGE}:${Image_Tag}'
            }
        }

        stage('Update Manifest') {
            steps {

                sh """
                sed -i 's|image: .*|image: $IMAGE_NAME:$IMAGE_TAG|g' k8s/deployment.yml
                """

                sh 'cat deployment.yaml'
            }
        }
        
        stage('Deploy to K8S') {
            steps {
                sh 'kubectl apply -f k8s/deployment.yml'
            }
        }
        
        stage('Wait for App') {
            steps {
                sh '''
                sleep 60
                kubectl get pods
                kubectl get svc
                '''
                }
            }
            
            stage('OWASP DAST SCAN') {
                steps {
                    sh '''
                    cd reports
                    docker run --rm ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://65.0.128.58:30412/Welcome
                    '''
            }
        }
    }
}
