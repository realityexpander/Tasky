package com.realityexpander.tasky.core.util

typealias Username = String     // ex: "realityexpander"
typealias Email = String        // ex: "chris@demo.com"
typealias Password = String     // ex: "Password1"
typealias AccessToken = String    // ex: "Bearer XXXXXXXXXXX...XXXXXXXXXX" (JWT Token)
typealias UuidStr = String      // ex: "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
typealias UserIdStr = String    // ex: "635dc7880806b27dc8ab81ae"
typealias UserId = UserIdStr    // ex: "635dc7880806b27dc8ab81ae"
typealias RefreshToken = String  // ex: "tmlULG4HvrDqE9IjLgwrDAtQQytkKYF0fgHp4mS7xXQKDpLgQTNUuOkEjGUbnyunLAIEmIymEHkc4lkk9hYOLGpCaTbHheZKvQGW12qXVDoItzPtr83HOV0ozx8amaRs"
typealias AccessTokenExpirationTimestampEpochMilli = Long // ex: 1672531199000 (epoch time in milliseconds)

fun accessToken(accessToken: String?): AccessToken? {
    return accessToken
}

fun email(email: String?): Email? {
    return email
}

fun password(password: String?): Password? {
    return password
}

fun username(username: String?): Username? {
    return username
}

fun userId(userId: String?): UserId? {
    return userId
}

fun uuidStr(uuidStr: String?): UuidStr? {
    return uuidStr
}

fun refreshToken(refreshToken: String?): RefreshToken? {
    return refreshToken
}


