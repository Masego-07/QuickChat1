# QuickChat
Student: Masego Mangwejane <st10512888>
Module: PROG5121 (Programming 1A)


A console chat app for PROG5121 (Programming 1A). Users register, log in, send, store or
disregard messages, and then search and report on the messages using arrays.

## Classes
| Class | Part | What it does |

| `Login` | Part 1 | Registers a user (username, password, cell number) and checks their login |
| `Message` | Part 2 | One message: ID, recipient and length checks, message hash, send/store/disregard, save to JSON |
| `MessageManager` | Part 3 | Keeps every message in arrays: sender/recipient list, longest message, search by ID or recipient, delete by hash, report, load from JSON |
| `QuickChat1` | Main | Runs the menus and brings the three parts together |

--How to run
Run `QuickChat1.java`. Register first (for example username `kyl_1`, password `Ch&&sec@ke99!`,
cell `+27838968976`), then log in with the same details.

Main menu: 1) Send Messages, 2) Show recently sent messages, 3) Quit, 4) Stored Messages.

--Stored Messages (Part 3)
Stored messages are saved to `storedMessages.json`. When the app starts, the file is read
back into the stored-messages array, so messages from earlier sessions are available in option 4.

--How to test
Run `LoginTest`, `MessageTest` and `MessageManagerTest`, or `mvn test` for all of them.
The tests also run automatically on GitHub Actions (`.github/workflows/TestJava.yml`) on every push.

-- References
- Google, "Gson User Guide," GitHub. Available: https://github.com/google/gson/blob/main/UserGuide.md
- Baeldung, "Validate Phone Numbers With Java Regex." Available: https://www.baeldung.com/java-regex-validate-phone-numbers
