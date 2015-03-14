# UNO ACM BattleCode

## Setup Instructions
### BattleCode
1. Download the installer from [this](http://www.battlecode.org/contestants/releases/) website
2.  Run installer by double clicking it (on windows)

### Bot
1. Open eclipse and create a new project (Files>New>Java Project)
1. Create a package with your team name (must be a single word) under src
1. Create a class named "RobotPlayer" under the package
1. Add the battlecode-server.jar and battlecode-client.jar to your eclipse build path
    1. Right click your project>Build Path>Configure Build Path>Libraries tab>Add external Jars
    1. Navigate to the lib folder inside of your battlecode install and select battlecode-server.jar and battlecode-client.jar

### Deploy 
1. Click the arrow next to the external tools>Select External tools configuration>Create a new ant build 
1. Download the deploy.xml from this repository and set it as the build file
1. Run 



### Hints
* You can use the template player [here](http://s3.amazonaws.com/battlecode-releases-2015/lectures/TemplateRobotPlayer.java) as a starting place (make sure to rename the package)
* Watch the MIT Battlecode 2015 finals [here](http://video.mit.edu/watch/battlecode-2015-final-tournament-29659/) for strategy ideas

## Links
* [Official website](http://www.battlecode.org)
* [Download battlecode](http://www.battlecode.org/contestants/releases/)
* [Lectures](http://www.battlecode.org/contestants/lectures/)
* [Battlecode api documentation](http://s3.amazonaws.com/battlecode-releases-2015/javadoc/index.html)
* [Battlecode rules/specification](https://github.com/battlecode/battlecode-server/blob/2015-1.2.3/specs.md)