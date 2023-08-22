# XMPPClient - Multi-Functional XMPP Chat Client

XMPPClient is a versatile Java-based chat client that utilizes the Extensible Messaging and Presence Protocol (XMPP) for communication. This project provides a comprehensive implementation of a client with features such as connecting to an XMPP server, managing contacts, sending/receiving messages, presence status, group chat, and file sharing. The core library used for XMPP communication is **Smack** version 4.2.0.

## Features

- `Connection Management`: Establish a connection to an XMPP server with configurable server details.

- `Login and Account Creation`: Log in with existing credentials or create a new account.
 
- `Contact Management`: View and manage contacts with their presence status.
 
- `Presence Status`: Set custom presence statuses, including availability, away, do not disturb, etc.
 
- `One-on-One Chat`: Send and receive text messages to/from contacts.
 
- `Group Chat`: Create group chats, invite users, and send messages within the group.
 
- `File Sharing`: Send and receive files during chats.
 
- `Message History`: Store and retrieve chat message history.
 
- `Notifications`: Receive notifications about friend connections and updates.

- `Subscription Management`: Accept incoming subscription requests.

## Dependencies

This project implements an XMPP client with various features for communication. It provides functionalities such as connecting to the server, managing contacts, sending and receiving messages, presence status management, and group chat capabilities. 

- smack-tcp (TCP connection)
- smack-extensions (Smack extensions)
- smack-im (Instant Messaging)
- smack-java7 (Java 7 support)

## Installation and Usage

1. **Prerequisites**: Ensure you have the following installed on your machine:
    - Java Development Kit (JDK) 8 or later
    - Gradle build tool (Optional, can be used to build and run the project)

2. **Clone the Repository**: Clone this repository to your local machine using Git:
```
git clone https://github.com/your-username/XMPPClient.git`
```
3. **Navigate to Project Directory**: Open a terminal and navigate to the cloned project directory:
```
cd XMPPClient
```
4. **Compile and Run**: Compile and run the project using Maven:
```
mvn compile exec:java
```

5. **Interact with the Application**: The XMPPClient application will start. You will be prompted to enter your XMPP server details, login credentials, and other actions based on your preferences.

6. **Use the Application**: Once logged in, you can interact with the application's features via the command-line interface. Use the provided commands to manage contacts, send messages, set presence status, create/join group chats, and more.

## Troubleshooting

If you encounter any issues while building or running the application, refer to the smack's documentation [Smack](https://github.com/igniterealtime/Smack).

## Contributing
Contributions are welcome! Feel free to submit issues or pull requests to help improve this XMPP client project. For more information, feel free to explore the code files or contact me.

## License

This project is licensed under the [MIT License](LICENSE).
