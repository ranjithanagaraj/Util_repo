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
    echo 'Deploy'
	sh  props.TOMCAT_DEPLOY+' '+props.TOMCAT_LOCATION

	}
	}
	
	stage('Email Notification')
	{
		commonUtility.sendEmail();
	}
}
return this
