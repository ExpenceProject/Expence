# Expence - finance application, uni project

## Table of contents
* [General info](#general-info)
* [Main features](#main-features)
* [Technologies](#technologies)
* [Architecture](#architecture)
* [Setup](#setup)
* [Miro documentation](#miro-documentation)
* [Creating a bill](#creating-a-bill)
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
To run the application, follow these steps:

- **Configuration file**:  
   Make sure you have a proper `.env` file located in the `client` folder.

- **Docker**:  
   Start the necessary services using Docker. Run the following command in the project root directory: `docker compose up -d`

- **Client**:  
   Navigate to the `client` folder and run the following commands:  
   - `npm install` to install the required dependencies.  
   - `npm run dev` to start the development server.  
   The application should now be running on `http://localhost:5173`.

- **Server**:  
   Navigate to the `server` folder and run the application using your IDE or the following command: `./gradlew bootRun`  
   The server should now be running on `http://localhost:8080`.

## Miro documentation
The project plan is organized in **Miro**, where the Kanban board tracks development progress, system functionalities are outlined, and the database model is visualized. It also includes an example view of the payment logic workflow for better clarity.

You can access the Miro board [here](https://miro.com/app/board/uXjVLKygdSg=/?share_link_id=926508286881).


## **Creating a Bill**

The process of creating a bill involves several steps, as outlined below:


### 1. **Find the Lender and Group**
Before creating a bill, we need to find the **lender** (the member lending the money) and the **group** where the bill will belong. If either is not found, an error is thrown.
```java
var lender = memberRepository.findByIdAndGroupIdOrThrow(Long.valueOf(billDto.lenderId()), Long.valueOf(billDto.groupId()));
var group = groupRepository.findByIdOrThrow(Long.valueOf(billDto.groupId()));
```


### 2. **Prepare the Bill**

Once the lender and group are identified, the bill is prepared using the data provided in the `CreateBillDto` object. This step involves setting the basic properties of the bill, such as its name, total amount, the group it belongs to, and the lender responsible for the payment. 

This logic is encapsulated in a method called `prepareBill`:

```java
private Bill prepareBill(CreateBillDto billDto, Member lender, Group group) {
    var bill = new Bill();
    bill.setName(billDto.name());
    bill.setTotalAmount(billDto.totalAmount());
    bill.setGroup(group);
    bill.setLender(lender);
    return bill;
}
```
### 3. **Find Borrowers in a Single Query**

To improve efficiency, borrowers are fetched in a single database query rather than iterating through individual IDs. This reduces the number of database calls and optimizes performance.

The borrowers' IDs are first extracted from the `CreateBillDto` object and collected into a set. Then, a single query retrieves all borrowers belonging to the specified group.

Here’s the implementation:

```java
private Map<String, Member> findBorrowers(CreateBillDto billDto) {
    Set<Long> borrowerIds = billDto.expenses().stream()
            .map(expense -> Long.valueOf(expense.borrowerId()))
            .collect(Collectors.toSet());

    return memberRepository.findAllByIdAndGroupIdOrThrow(borrowerIds, Long.valueOf(billDto.groupId())).stream()
            .collect(Collectors.toMap(member -> String.valueOf(member.getId()), Function.identity()));
}
```

### 4. **Prepare Expenses**

After retrieving the borrowers, we proceed to prepare the expenses for the bill. The logic checks if an expense for a particular borrower already exists for the given bill. If it does not exist, a new `Expense` is created and associated with the bill. If the expense already exists, we update the existing one.

Here’s the implementation:

```java
private void prepareExpenses(Set<CreateExpenseDto> newExpenses, Bill bill, Map<String, Member> borrowers) {
    for (CreateExpenseDto dto : newExpenses) {
        var doesExist = bill.getExpenses().stream().anyMatch(expense -> expense.getBorrower().getId().equals(dto.borrowerId()));
        
        if (!doesExist) {
            var expense = new Expense();
            expense.setAmount(dto.amount());
            expense.setBorrower(borrowers.get(dto.borrowerId()));
            expense.setBill(bill);
            bill.addExpense(expense);
        } else {
            bill.getExpenses().stream()
                .filter(expense -> expense.getBorrower().getId().equals(dto.borrowerId()))
                .findFirst()
                .ifPresent(expense -> {
                    expense.setAmount(dto.amount());
                    expense.setBorrower(borrowers.get(dto.borrowerId()));
                    expense.setBill(bill);
                });
        }
    }
}
```

### 5. **Save the Bill**

Once the bill and its associated expenses are prepared, the final step is to save the bill to the database. This is done by calling the repository’s `saveOrThrow` method, which persists the bill and any related entities (like expenses) in the database.

Here’s the implementation:

```java
return billRepository.saveOrThrow(bill).dto();
```

## Authors
Project created by:
- Karol Wiśniewski
- Piotr Damrych
- Aleksandra Mordzon
- Justyna Towarnicka
