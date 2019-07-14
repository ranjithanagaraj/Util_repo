def function(props) {
	stage('CheckoutProject') {
		app_url =props.JAVA_APP_REPO_GIT_URL		
		git "${app_url}"
		pom = readMavenPom file: props.POM_FILE
		artifactId=pom.artifactId
		echo "${artifactId}"
		version=pom.version
		
	}
	
stage('BuildProject') {
		sh props.MAVEN_BUILD		
    }
	stage('SonarAnalysis'){
		commonUtility.sonar();
	}

	
	stage('UploadArtifactory') {
		commonUtility.uploadWarArtifactory();
	
	}
	
    stage('Docker deploy')
   {
    sh props.DOCKER_CMD
   sh props.DOCKER_RUN
   }
	
stage('Prod Deploy') {
	def Deploy = false;
	try {
		input message: 'Deploy?', ok: 'Deploy'
		Deploy = true
		} catch (err) {
	Deploy = false
}
if(Deploy)
{
    echo 'Deploy to kubernetes'
     sh 'ssh admin@13.232.185.135 kubectl get all'
     sh 'ssh admin@13.232.185.135 kubectl apply -f app.yaml -f redis.yaml --recursive'
     sh 'ssh admin@13.232.185.135 kubectl get all'
	}
	}
	
	stage('Email Notification')
	{
		commonUtility.sendEmail();
	}
}
return this
