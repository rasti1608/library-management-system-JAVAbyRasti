# Railway Deployment Guide for Spring Boot Projects

## Overview
This guide explains how to deploy your Spring Boot application (built locally in IntelliJ) to Railway for production hosting. Railway detects Spring Boot projects automatically and handles the build process.

## ⚠️ IMPORTANT: Branch Selection for Railway

**For Railway deployment, you MUST use the `working_Final_v.2.0-sept20` branch.**

**Why?**
- Railway and similar cloud platforms restrict file system write access outside specific directories
- The `working_Final_v.2.0-sept20` branch has JSON data files positioned correctly for cloud hosting
- The `main` branch is optimized for local development where file system access is unrestricted

**Don't waste time debugging** - if you need to redeploy to Railway, always use the `working_Final_v.2.0-sept20` branch!

---

## Prerequisites

### What You Need Before Starting
1. **Working Spring Boot project in IntelliJ**
   - Application runs successfully on `localhost:8080`
   - All features tested and working locally
   
2. **GitHub account**
   - Your code must be in a GitHub repository
   
3. **Railway account**
   - Sign up at https://railway.com/
   - Free trial available, then $5/month for paid plan

---

## Part 1: Prepare Your Project for Deployment

### Step 1: Verify Your Project Structure
Your Spring Boot project should have this structure:
```
your-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourpackage/
│   │   │       ├── YourApplication.java
│   │   │       ├── controllers/
│   │   │       ├── models/
│   │   │       └── services/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data/
│   │           ├── books.json
│   │           ├── users.json
│   │           └── rentals.json
├── pom.xml (for Maven)
└── README.md (optional)
```

### Step 2: Check Your `pom.xml`
Make sure your `pom.xml` has the Spring Boot Maven plugin:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Step 3: Verify Port Configuration
In `application.properties`, ensure your app uses Railway's dynamic port:
```properties
# Railway will set the PORT environment variable
server.port=${PORT:8080}
```
This means: "Use PORT from environment variable, default to 8080 if not set"

### Step 4: Update CORS Configuration (IMPORTANT)
In your Spring Boot config, update CORS to allow your frontend domain:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",           // Local development
                    "https://your-frontend.netlify.app" // Production frontend
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
```

---

## Part 2: Push Your Code to GitHub

### Step 1: Create a GitHub Repository
1. Go to https://github.com/
2. Click the **"+"** icon → **"New repository"**
3. Name it (e.g., `library-management-system`)
4. Set to **Public** or **Private** (your choice)
5. **DO NOT** initialize with README (if you already have code)
6. Click **"Create repository"**

### Step 2: Push Your Local Code to GitHub

**Option A: If starting fresh (no Git initialized yet)**
```bash
cd /path/to/your/project
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/yourusername/your-repo-name.git
git push -u origin main
```

**Option B: If you already have a local Git repo**
```bash
cd /path/to/your/project
git remote add origin https://github.com/yourusername/your-repo-name.git
git push -u origin main
```

### Step 3: Verify Upload
Go to your GitHub repository URL and verify all your files are there:
- `src/` folder with all Java classes
- `pom.xml`
- `application.properties`
- Data files (books.json, users.json, etc.)

---

## Part 3: Deploy to Railway

### Step 1: Create a Railway Account
1. Go to https://railway.com/
2. Click **"Sign up"**
3. Choose **"Sign up with GitHub"** (easiest option)
4. Authorize Railway to access your GitHub

### Step 2: Create a New Project
1. Click **"New Project"**
2. Select **"Deploy from GitHub repo"**
3. **If this is your first time:** Railway will ask for GitHub permissions
   - Click **"Configure GitHub App"**
   - Select which repositories Railway can access
   - Choose either:
     - **All repositories** (easiest)
     - **Only select repositories** → Select your Spring Boot project
   - Click **"Save"**

### Step 3: Select Your Repository AND BRANCH
1. After GitHub authorization, you'll see a list of your repositories
2. Find and click on your Spring Boot project (e.g., `library-management-system-Rasti`)
3. **CRITICAL:** Make sure to select the **`working_Final_v.2.0-sept20`** branch, NOT main!
   - After selecting the repo, Railway may ask which branch to deploy
   - Or you can change it in Settings → Source → Branch connected to production
4. Railway will automatically start deploying

### Step 4: Wait for Build to Complete
Railway will:
1. **Detect** it's a Spring Boot/Maven project
2. **Run** `mvn clean install` to build your application
3. **Create** a Docker container
4. **Start** your application

**Build time:** Usually 2-5 minutes

**Watch the logs:**
- Click on the deployment card
- Go to **"Build Logs"** tab to see progress
- Look for "BUILD SUCCESS" message

### Step 5: Generate a Public Domain
Once the build succeeds:
1. Click on your service/deployment
2. Go to **"Settings"** tab (or look for **"Networking"**)
3. Find **"Public Networking"** section
4. Click **"Generate Domain"**

Railway will give you a URL like:
```
https://library-management-system-rasti-production.up.railway.app
```

**This is your public API URL!** Save this URL.

### Step 6: Test Your Deployment
Test your API endpoints:
```
# Health check
https://your-app-name.railway.app/api/health

# Get books
https://your-app-name.railway.app/api/books

# Swagger UI (if enabled)
https://your-app-name.railway.app/swagger-ui.html
```

---

## Part 4: Updating Your Deployment (Future Changes)

### When You Make Code Changes Locally

**Step 1: Make changes in IntelliJ**
- Edit your Java files
- Test locally to make sure everything works

**Step 2: Commit and push to GitHub**
```bash
git add .
git commit -m "Description of your changes"
git push origin main
```

**Step 3: Railway automatically redeploys**
- Railway watches your GitHub repository
- When you push changes, Railway automatically:
  - Pulls the new code
  - Rebuilds the application
  - Redeploys it
- **No manual action needed!**

**Step 4: Verify the update**
- Go to Railway dashboard
- Watch the deployment status
- Test your API once it's live

---

## Part 5: Connecting Your Frontend

### Update Frontend API URL
If you have an HTML/JavaScript frontend, update the API base URL:

**Before (Local Development):**
```javascript
const API_BASE_URL = 'http://localhost:8080';
```

**After (Production):**
```javascript
const API_BASE_URL = 'https://library-management-system-rasti-production.up.railway.app';
```

### Deploy Frontend to Netlify (Optional)
1. Go to https://www.netlify.com/
2. Drag and drop your frontend folder
3. Or connect it to a separate GitHub repository
4. Your frontend will get a URL like: `https://your-app.netlify.app`

### Update CORS in Spring Boot
Remember to add your Netlify URL to the CORS configuration in your Spring Boot app (see Part 1, Step 4).

---

## Troubleshooting Common Issues

### The Golden Rule: When in Doubt, Roll Back

**If your deployment was working before and now it's broken:**

Don't waste hours debugging! Instead, roll back to the last working version:

1. **Identify the working commit** - Look at your GitHub commit history for messages like "Final v2.0 retaining session and working fully on railway.app"
2. **Create a branch from that commit:**
   - Go to GitHub → Click on the working commit
   - Click "Browse files" (or `<>`)
   - In the branch dropdown, type a new branch name (e.g., `working-rollback`)
   - Create the branch
3. **Deploy that branch on Railway:**
   - Railway → Service → Settings → Source
   - Change "Branch connected to production" to your rollback branch
   - Click Deploy

**This saves you from hours of debugging when the issue is just "you deployed the wrong version."**

### Common Issues and Solutions

### Issue 1: Build Fails
**Error:** "Error creating build plan with Railpack"

**Solution:**
1. Verify `pom.xml` has Spring Boot Maven plugin
2. Check that your project structure is correct
3. Try clicking **"Redeploy"** from the three-dot menu

### Issue 2: Authentication Not Working / Users Not Loading
**Error:** Login fails, "User not found", or new registrations work but existing users don't

**Solution:**
**You're probably deploying the wrong branch!** Make sure Railway is deploying the `working_Final_v.2.0-sept20` branch, NOT main.

To check/fix:
1. Go to Railway → Your service → Settings → Source
2. Look at "Branch connected to production"
3. If it says "main", change it to `working_Final_v.2.0-sept20`
4. Click "Deploy" to redeploy with the correct branch

### Issue 3: Application Starts But Can't Access It
**Error:** Can't reach your API URL

**Solution:**
1. Make sure you generated a public domain (Step 5 in Part 3)
2. Check that `server.port=${PORT:8080}` is in `application.properties`
3. Look at "Deploy Logs" for error messages

### Issue 4: CORS Errors from Frontend
**Error:** "Access-Control-Allow-Origin" error in browser console

**Solution:**
1. Add your frontend URL to CORS configuration
2. Ensure `.allowCredentials(true)` is set
3. Redeploy after making CORS changes

### Issue 5: Data Files Not Loading
**Error:** Books or users not showing up

**Solution:**
1. **Make sure you're using the `working_Final_v.2.0-sept20` branch!**
2. Verify JSON files are in `src/main/resources/data/`
3. Check file paths in your Java code
4. Look at Railway logs for file loading errors

### Issue 6: Authentication Not Working
**Error:** Login fails or sessions don't persist

**Solution:**
1. Check that session configuration is correct
2. Verify CORS allows credentials
3. Make sure frontend sends credentials with requests:
   ```javascript
   fetch(url, {
       credentials: 'include'
   })
   ```

---

## Railway Dashboard Quick Reference

### Key Sections

**1. Overview Tab**
- Shows deployment status
- Activity log
- Quick links to logs

**2. Deployments Tab**
- List of all deployments
- Click to see details, logs, redeploy

**3. Settings Tab**
- Environment variables
- Public networking (domain generation)
- Service configuration

**4. Build Logs Tab**
- Shows Maven build output
- Useful for debugging build failures

**5. Deploy Logs Tab**
- Shows application startup logs
- Spring Boot initialization messages
- Runtime errors appear here

---

## Cost and Billing

### Free Trial
- Railway offers a free trial with limited resources
- Good for testing and small projects

### Paid Plan ($5/month minimum)
- Starts at $5/month
- Pay for what you use
- Includes:
  - 512 MB RAM
  - Automatic deployments
  - Custom domains
  - SSL certificates

### Usage Tips
- Railway charges for active time
- Unused services are automatically paused to save costs
- Monitor usage in the billing section

---

## Best Practices

### 1. Keep Secrets Safe
- **Never commit passwords or API keys to GitHub**
- Use Railway's environment variables for secrets:
  1. Go to your service in Railway
  2. Click **"Variables"** tab
  3. Add environment variables like `DB_PASSWORD=xxx`
  4. Access in Java: `System.getenv("DB_PASSWORD")`

### 2. Use Branches for Testing
- Create a `dev` branch for testing
- Keep `main` branch stable
- Railway can deploy from any branch

### 3. Monitor Your Logs
- Check logs regularly for errors
- Railway keeps logs for debugging
- Use logging in your Spring Boot app:
  ```java
  private static final Logger logger = LoggerFactory.getLogger(YourClass.class);
  logger.info("Important event happened");
  ```

### 4. Test Locally First
- Always test changes locally before pushing
- Run `mvn clean install` to verify build works
- Test all endpoints in Postman or browser

### 5. Document Your API
- Include Swagger/OpenAPI documentation
- Makes it easier to test and share
- Clients know exactly how to use your API

---

## Quick Command Reference

### Git Commands
```bash
# Initialize repository
git init

# Check status
git status

# Add all files
git add .

# Commit changes
git commit -m "Your message"

# Push to GitHub
git push origin main

# Pull latest changes
git pull origin main

# Create new branch
git checkout -b new-branch-name
```

### Maven Commands (Run Locally)
```bash
# Build project
mvn clean install

# Run Spring Boot app
mvn spring-boot:run

# Run tests
mvn test

# Package as JAR
mvn package
```

### Testing API Endpoints (Local)
```bash
# Using curl
curl http://localhost:8080/api/health

# Or just open in browser
http://localhost:8080/api/books
```

---

## Summary Checklist

### Before Deployment
- [ ] Project builds successfully in IntelliJ
- [ ] All features work on localhost:8080
- [ ] `pom.xml` has Spring Boot Maven plugin
- [ ] `server.port=${PORT:8080}` in application.properties
- [ ] CORS configured for production frontend URL
- [ ] Code committed and pushed to GitHub

### During Deployment
- [ ] Railway account created and linked to GitHub
- [ ] New project created in Railway
- [ ] Repository selected and deployment started
- [ ] Build completed successfully
- [ ] Public domain generated
- [ ] API endpoints tested and working

### After Deployment
- [ ] Frontend updated with Railway API URL
- [ ] CORS settings verified
- [ ] All features tested in production
- [ ] Logs checked for any errors
- [ ] URL saved for future reference

---

## Need Help?

### Railway Documentation
- Official docs: https://docs.railway.app/
- Community: https://railway.app/discord

### Spring Boot Resources
- Spring Boot docs: https://spring.io/projects/spring-boot
- Guides: https://spring.io/guides

### Your Previous Deployment
- **Railway URL:** `library-management-system-rasti-production.up.railway.app`
- **GitHub Repo:** `rasti1608/library-management-system-Rasti`
- **Working Branch for Railway:** `working_Final_v.2.0-sept20`
- **Last successful deployment:** September 2025

### CRITICAL: Use the Correct Branch for Railway
**For Railway deployment, use the `working_Final_v.2.0-sept20` branch, NOT the main branch.**

The main branch works perfectly for local development, but Railway requires JSON data files to be in specific locations due to file system restrictions. The `working_Final_v.2.0-sept20` branch has the correct configuration for Railway's environment.

---

## That's It!

This entire process from local development to production takes about **10-15 minutes** once you're familiar with it.

**Remember:**
1. Code locally in IntelliJ
2. Push to GitHub
3. Railway deploys automatically
4. Get public URL and test

Every time you make changes, just push to GitHub and Railway handles the rest!
