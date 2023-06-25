# BankPlugin for Minecraft

BankPlugin is a Minecraft plugin that allows players to create and manage
virtual banks and accounts within the game. It provides a range of
features, including account creation, depositing and withdrawing funds,
and setting interest rates.

## Features

- **Account Management**: Players can create accounts, check their
balance, deposit funds, and withdraw funds.
- **Bank Management**: Players can create and delete banks, set interest
rates, and manage accounts within their bank.
- **ATM Interface**: The plugin provides an ATM-like interface for easy
management of accounts and banks.
- **Interest Rates**: Banks can set interest rates, providing a way for
players to earn interest on their deposits.

## Commands

### Account Commands

- `/account accept`: Accepts an account request.
- `/account deposit <amount>`: Deposits the specified amount into your
account.
- `/account balance`: Shows the balance of your account.
- `/account withdraw <amount>`: Withdraws the specified amount from your
account.

### Bank Commands

- `/bank create <name>`: Creates a new bank with the specified name.
- `/bank delete <name>`: Deletes the specified bank.
- `/bank deposit <name> <amount>`: Deposits the specified amount into the
specified bank.
- `/bank accountrequest <name>`: Sends a request to create an account in
the specified bank.
- `/bank setinterest <name> <rate>`: Sets the interest rate for the
specified bank.
- `/bank balance <name>`: Shows the balance of the specified bank.
- `/bank withdraw <name> <amount>`: Withdraws the specified amount from
the specified bank.

### ATM Feature

The `ATM_Listener.java` class handles events related to ATMs. It includes
methods to check if a block is an ATM, if a player owns an ATM, and to
find the closest hologram (likely used for displaying information at the
ATM). It also handles events for breaking blocks, clicking blocks, and
changing signs, which are likely used for interacting with the ATM.

Players can interact with ATMs by clicking on them,
in order to interact with their bank account.

## Installation

To install the plugin, download the latest release and add it to your
server's `plugins` directory. Then, restart your server.

## Contributing

Contributions are welcome! Please open an issue to discuss your ideas or
submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
