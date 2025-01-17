# Expence - finance application, uni project

## Table of contents
* [General info](#general-info)
* [Main features](#main-features)
* [Technologies](#technologies)
* [Architecture](#architecture)
* [Setup](#setup)
* [Authors](#authors)

## General info
Expence is a web application designed to simplify tracking group expenses, ensuring everyone pays or receives the correct amount effortlessly.

## Main features
- **Manage Billing Group**: Create and manage groups for tracking shared expenses effortlessly.  
- **Create a Bill**: Add detailed bills with customizable amounts and participants.  
- **Create a Payment**: Log and track payments between group members.  
- **Invitation Notification**: Receive and send notifications to invite members to join billing groups.  
- **View Group Balance**: Access a detailed summary of balances within the group to see who owes or is owed.  
- **Create Profile**: Set up a personalized profile to track your activity and preferences.  


## Technologies
Project is created with:
- **TypeScript**: Typed JavaScript for improved code quality.  
- **React**: Library for building user interfaces.  
- **Java**: Backend programming language.  
- **Spring Boot**: Framework for building Java applications.  
- **PostgreSQL**: Relational database system.  
- **MinIO**: Object storage for files and data. 
- **JUnit**: Testing framework for Java, used to ensure the reliability of backend logic.  

  
## Architecture
The application is divided into two main components:  

- **Client:** The frontend, built with React and TypeScript, handles the user interface and communicates with the backend via APIs.  

- **Server:** The backend, developed in Java with Spring Boot, follows a **hexagonal architecture**. This design ensures a clean separation between the business logic and external systems (e.g., database, API clients), making the application modular, maintainable, and easy to test.

## Setup

## Authors
Project created by:
- Karol Wiśniewski
- Piotr Damrych
- Aleksandra Mordzon
- Justyna Towarnicka
