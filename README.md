# app_promotion_test

To get a Git project into your build:

Step 1 : Add it in your root build.gradle at the end of repositories:

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  
Step 2 : Add the dependency
dependencies {
	        compile 'com.github.himalorafox:app_promotion_test:v1.1'
	}
  
  
Step 3 : Inislize library in your main activity

 new AppPromotion(activity ,you package name , interval in minute);
 
 ex:  new AppPromotion(this,"com.example.app",1);
