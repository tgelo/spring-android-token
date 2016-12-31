# Spring Token Based Authentication Server

## Overview
The goal of the project is to show token based authentication using spring
Technical details
* Java8
* Spring boot
* Liquibase
* Ehcache
* Gradle
* Spock

## Usage

Server can be run using:
 1. _./gradlew bootRun_ command 
 2. _java -jar spring-server-1.0-SNAPSHOT.jar_
 3. Run main() method in Application class
## Test

Unit tests are written using spock.
There is also **curl_test.sh** script which test controllers using CURL while server is running.
