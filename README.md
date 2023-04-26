# Slacker Chat - A Simple UDP Chat Application

Slacker Chat is a fun and lightweight college project that provides a simple chat platform for people to connect and communicate over their local network in real-time! We've created this application using Java for both the client and server side, and Java Swing for the GUI. Project by me and [Oluwaseun](https://github.com/Oluwaseun-Adediwura/Oluwaseun-Adediwura)

## How It Works

Slacker Chat allows users to discover chat servers on their local network, connect to them, and send messages to each other in a simple and intuitive interface. The application consists of two main components: the `SlackerClient` and the `Server`. The `SlackerClient` is responsible for providing a user interface and handling user input, while the `Server` listens for incoming messages, manages connections, and relays messages between connected clients.

We also have two additional classes, `Message` and `ClientInfo`. The `Message` class represents individual chat messages and includes the sender's username and the message content. The `ClientInfo` class stores information about connected clients, such as their username, IP address, and port.

## Getting Started

To run Slacker Chat, follow these simple steps:

1. Clone this repository to your local machine.
2. Compile the Java source files for the client, server, and supporting classes: `SlackerClient.java`, `Server.java`, `Message.java`, and `ClientInfo.java`.
3. Run the `Server` by executing the compiled `Server.class` file.
4. Run one or more instances of the `SlackerClient` by executing the compiled `SlackerClient.class` file.
5. Connect the clients to the server, enter your username, and start chatting away!

## Connect via Hotspot

To easily connect multiple clients to Slacker Chat using a hotspot, follow these steps:

1. On the computer running the `Server`, create a hotspot by following the instructions for your operating system.
2. On each client device, join the hotspot created by the server computer.
3. Launch the `SlackerClient` on each client device, and it will automatically discover and connect to the server on the local network.
4. Enter your username and start chatting with others connected to the same hotspot network.

Happy chatting!

## A Witty Description

Our Slacker Chat is like a digital water cooler, providing a space for people to gather and share their thoughts, ideas, and casual banter. With its simple yet effective design, Slacker Chat is perfect for those moments when you just need a break from the daily grind. It may not have all the bells and whistles of other chat applications, but it's got charm and character in spades. So grab a cup of coffee, pull up a chair, and start slacking off with Slacker Chat!

A big thank you to GPT-4 for helping us write this README file! 🤖✍️
