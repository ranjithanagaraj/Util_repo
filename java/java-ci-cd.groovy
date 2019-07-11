def function(props) {
	stage('CheckoutProject') {
		app_url =props.JAVA_APP_REPO_GIT_URL		
		git "${app_url}"
		pom = readMavenPom file: props.POM_FILE
		artifactId=pom.artifactId
		echo "${artifactId}"
		version=pom.version
		
	}
	
	stage('SonarAnalysis'){
		commonUtility.sonar();
	}
	stage('BuildProject') {
		/*sh props.SONAR_SCAN+' '+props.SONAR_HOST*/
		sh props.MAVEN_BUILD
		
    }
	
	stage('UploadArtifactory') {
		commonUtility.uploadWarArtifactory();
	
	}
	stage('Tomcat Installation ') {
	def Install = false;
	try {
		input message: 'Install?', ok: 'Install'
		Install = true
		} catch (err) {
	Install = false
	
	}
	
       if (Install){   
	 
	  sh props.DOCKER_CMD
        }
    }	
	stage('Deploying to Tomcat'){
		sh  props.TOMCAT_DEPLOY+' '+props.TOMCAT_LOCATION
	}
	
	stage('Email Notification')
	{
		commonUtility.sendEmail();
	}
}
return this
