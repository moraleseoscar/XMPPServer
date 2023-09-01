# XMPPClient - Multi-Functional XMPP Chat Client

XMPPClient is a versatile Java-based chat client that utilizes the Extensible Messaging and Presence Protocol (XMPP) for communication. This project provides a comprehensive implementation of a client with features such as connecting to an XMPP server, managing contacts, sending/receiving messages, presence status, group chat, and file sharing. The core library used for XMPP communication is **Smack** version 4.2.0.

## Features and Lessons Learned

### Feature Implementation Challenges and Lessons

This section delves into both the implemented features and the challenges encountered during the development of XMPPClient. Each feature presented its unique set of difficulties and opportunities for learning.

- `Connection Management`: Establishing a robust connection to an XMPP server was essential. Implementing reconnection strategies and handling connection failures required in-depth understanding of network programming and error handling.

- `Login and Account Creation`: Designing a seamless login process and user-friendly account creation flow demanded careful consideration of user experience and authentication mechanisms.

- `Contact Management`: Implementing a reliable contact management system was more intricate than anticipated. Dealing with real-time updates and synchronization was a significant challenge.

- `Presence Status`: Allowing users to set and display custom presence statuses required integrating XMPP's complex presence management system, which proved to be a substantial technical hurdle.

- `One-on-One Chat`: Developing the message exchange system involved grappling with the asynchronous nature of XMPP communication and incorporating message acknowledgments for enhanced reliability.

- `Group Chat`: Enabling group chats necessitated intricate message routing mechanisms and synchronization techniques to ensure smooth communication within groups.

- `File Sharing`: Implementing a secure and efficient file sharing feature while considering various file types and sizes posed a technical challenge. Ensuring data integrity and handling interruptions were critical.

- `Message History`: Storing and retrieving chat histories involved working with databases and efficiently managing large amounts of data, which required a deep dive into database integration.

- `Notifications`: Integrating real-time notifications for friend connections and updates required a thorough understanding of XMPP's event system and push mechanisms.

- `Subscription Management`: Developing the subscription system involved understanding XMPP's subscription workflow and implementing logic to manage incoming requests.

### Learning from Challenges

The most significant challenges revolved around implementing event listeners to manage messages, notifications, and files. These posed difficulties as I hadn't recently worked extensively with event-driven programming. Structuring the project for optimal usability and a seamless user experience also presented considerable difficulty. Choosing Java as the programming language came after experimenting with JavaScript and Python, but it proved to be the optimal choice upon achieving successful connection establishment.

Addressing these challenges entailed revisiting documentation, leveraging AI tools for enhanced understanding, and engaging with peers to discuss optimal solutions.

The project's success in overcoming these hurdles underscored the value of perseverance, collaborative problem-solving, and continuous learning in software development.

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


