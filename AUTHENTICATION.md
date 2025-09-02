# Authentication Module

This authentication system supports multiple login methods per user account:
- Email/Password authentication
- Google OAuth2
- Apple Sign-In
- Spotify OAuth2

## Architecture

### Entities
- `User`: Core user information (name, email, profile picture)
- `UserAuthentication`: Authentication methods linked to a user
- `AuthProvider`: Enum for different authentication providers

### Key Features
- **Multi-provider support**: Users can link multiple authentication methods
- **Secure separation**: User data separated from authentication credentials
- **JWT tokens**: Stateless authentication with configurable expiration
- **OAuth2 integration**: Full support for social login providers

## API Endpoints

### Email/Password Authentication
```
POST /auth/register - Register with email/password
POST /auth/login - Login with email/password
```

### OAuth2 Authentication
```
GET /auth/oauth2/{provider} - Get OAuth2 authorization URL
GET /auth/success?token={token} - OAuth2 success callback
GET /auth/error - OAuth2 error callback
```

### Account Management
```
GET /auth/methods/{userId} - Get user's linked authentication methods
POST /auth/link/{provider} - Link new authentication method
DELETE /auth/unlink/{provider} - Unlink authentication method
```

## OAuth2 Providers Configuration

### Google
1. Create project at https://console.developers.google.com
2. Enable Google+ API
3. Create OAuth2 credentials
4. Set redirect URI: `http://localhost:9000/login/oauth2/code/google`

### Apple
1. Register at https://developer.apple.com
2. Create Service ID and Key
3. Configure Sign-In domain
4. Set redirect URI: `http://localhost:9000/login/oauth2/code/apple`

### Spotify
1. Create app at https://developer.spotify.com
2. Get Client ID and Secret
3. Set redirect URI: `http://localhost:9000/login/oauth2/code/spotify`

## Environment Variables

Copy `.env.example` to `.env` and configure:

```bash
# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
APPLE_CLIENT_ID=your-apple-service-id
APPLE_CLIENT_SECRET=your-apple-client-secret
SPOTIFY_CLIENT_ID=your-spotify-client-id
SPOTIFY_CLIENT_SECRET=your-spotify-client-secret
```

## Usage Examples

### Frontend Integration

#### OAuth2 Login
```javascript
// Get OAuth2 authorization URL
const response = await fetch('/auth/oauth2/google');
const { authUrl } = await response.json();

// Redirect user to provider
window.location.href = authUrl;

// Handle success callback
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');
localStorage.setItem('authToken', token);
```

#### Link Additional Authentication Methods
```javascript
// Link Google account to existing user
await fetch('/auth/link/google', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${userToken}` },
  body: new URLSearchParams({
    userId: currentUser.id,
    email: userEmail,
    providerId: googleUserId
  })
});
```

### Database Schema

#### users collection
```javascript
{
  _id: ObjectId,
  name: String,
  lastName: String,
  email: String,
  profile_picture_url: String
}
```

#### user_authentications collection
```javascript
{
  _id: ObjectId,
  user_id: String,
  provider: "EMAIL" | "GOOGLE" | "APPLE" | "SPOTIFY",
  provider_id: String, // OAuth provider user ID
  email: String,
  password_hash: String, // Only for EMAIL provider
  created_at: Date,
  last_used_at: Date,
  is_active: Boolean
}
```

## Security Features

- Passwords hashed with BCrypt
- JWT tokens with configurable expiration
- Unique indexes on email+provider combinations
- Protection against account linking vulnerabilities
- Support for unlinking authentication methods (minimum 1 required)

## Testing

The system includes comprehensive unit tests for:
- User registration and login flows
- OAuth2 authentication handlers
- Multi-provider account linking
- Security validations