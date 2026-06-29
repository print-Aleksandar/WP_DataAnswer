# DataAnswering – System Features and Architecture
## Overview

DataAnswering is a web-based conversational AI platform built using a hybrid chatbot architecture inspired by modern large language model applications such as ChatGPT and Claude. The system supports both authenticated and anonymous users while providing configurable usage limits, chat history management, subscription plans, and data retention mechanisms suitable for AI-powered applications.

## Hybrid Authentication Architecture

The application implements a hybrid authentication model that allows both authenticated and anonymous users to interact with the system.

### Anonymous Users

Users who choose not to authenticate are automatically assigned a temporary account associated with their current session. These temporary users receive a dedicated guest subscription plan with strict usage limitations.

Guest users can:

- Create an unlimited number of chat sessions.
- Submit a limited number of prompts due to a very restrictive token allowance.
- Interact with the system without registration.

To minimize resource consumption, only the most recent guest chat is retained during the active session, while previous chats are automatically removed. Once the user session expires, all information linking the temporary account to the user is permanently deleted from the system.

### Authenticated Users

Authenticated users receive persistent accounts with configurable subscription plans.

Registered users can:

- Access chat history.
- Return to previously created conversations.
- Maintain a configurable number of recent chats.
- Delete individual or multiple chats from their personal workspace.

The system maintains a history of the user's most recent N conversations, where N is configurable by the application administrator.

Chat Persistence Model

The system distinguishes between two types of chat entities:

- Temporary Chats - which belong to anonymous users and exist only during the active session.
- Saved Chats - which belong to authenticated users and persist after logout.

When a user removes a chat from their interface, either manually or automatically due to history limitations:

- The chat itself remains stored in the database.
- All associated entities (prompts, responses, uploaded files, tool calls, metadata, etc.) remain preserved.
- Only the association between the user and the chat is removed.

This approach enables data retention for future system improvements and analytics while providing users with control over their visible chat history.

## Token Limiting System

Each subscription plan defines a maximum token allowance.

The application implements a configurable rolling-window token limiting mechanism:

- The system continuously calculates the total number of tokens consumed by a user during the previous N hours.
- Once the configured token threshold is exceeded, the user loses the ability to submit prompts.
- The restriction applies to both existing and newly created chats.
- Access is automatically restored after the configured cooldown period expires.

Anonymous users are assigned a significantly smaller token allowance per chat session. Since token consumption grows rapidly with conversation length, guest accounts are generally limited to one or two prompts per chat session. However, guests may create an unlimited number of separate chats. This behavior closely resembles the hybrid access policies used by modern conversational AI platforms.

## Subscription Plans

The system supports three subscription plans:

- __Guest Plan__ - Available to anonymous users with highly restricted token usage
- __Standard Plan__ - Available to authenticated users with moderate token limits
- __Premium Plan__ - Available to authenticated users with increased token limits

The application maintains a complete subscription history for each authenticated user, allowing tracking of subscription changes over time.

## Chat Management
### Delete Individual Chat

Users can remove a single chat directly from the sidebar interface.

This operation:

- Removes the chat from the user's visible history.
- Preserves the chat and all related entities in the database.

### Delete All Chats

Authenticated users can delete all chats from their account through the user settings page.

This operation:

- Removes all chat associations from the user's account.
- Does not physically delete chat data from the database.

## Soft Account Deletion

Authenticated users may permanently deactivate their account at any time after explicit confirmation.

The account deletion process performs a soft delete operation:

- Removed information
- First and last name
- Email address
- Authentication credentials
- User profile information
- Preserved information
- Chat prompts
- AI responses
- Uploaded files
- Generated metadata
- Historical conversation data

This retention strategy reflects the practices of modern AI applications, where users must explicitly consent to data retention policies during account creation.

## Architectural Characteristics

The system incorporates several architectural concepts inspired by contemporary conversational AI platforms:

- Hybrid authenticated/anonymous access model
- Session-based temporary accounts
- Persistent chat history management
- Rolling-window token limitation
- Configurable subscription plans
- Soft deletion strategies
- Historical subscription tracking
- Data retention for continuous platform improvement
- Separation between user ownership and conversation persistence

### Interacting with the LLM

The Spring application exposes two endpoints for generating model responses:

- `/api/prompt` - Generates a response for a user prompt.
- `/api/regenerate/last` - Removes the most recent response (if one exists) and generates a new one. This is primarily used when a chat is first created and an initial response needs to be generated.

Both endpoints stream events to the frontend as JSON chunks. These chunks contain the generated tokens, tool-calling events and token limit notifications. The frontend parses the streamed chunks and updates the DOM in real time.

The contoller handling these endpoints uses the `LlmService` class to comunicate with the `llm-service` container. This microservice is responsible for model interaction, tool calling and tool execution. It exposes an `/ask` endpoint that streams events using the same format consumed by the Spring application. When a request is received, it is provided with the user id, chat id, the chat history and the user's prompt. The service runs an agent loop that generates a response back to the Spring application, which then restructures and forwards it to the frontend. 