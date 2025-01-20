CS2 stats application (updated 2025-01-20)

1. Clone the repository and open the project (I use IntelliJ IDEA).
2. Start Docker engine (I use Docker Desktop).
3. Start Cs2statsApplication.

The application and database (mydatabase) should now be up and running. 
Information about the database and settings can be found in: 
* application.properties
* compose.yaml

How does the application work at the moment? 

1. DirectoryWatcherService
   Monitors the directory for new .log files.
   Passes the log file path to LogParserService for processing.

2. LogParserService
   Parses the log file to extract structured data.
   Updates the database with player and match information.

3. Database
   Acts as the final repository for all information, such as match results, player statistics and team outcomes. 
