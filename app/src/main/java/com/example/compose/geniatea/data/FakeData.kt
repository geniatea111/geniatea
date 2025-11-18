/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.geniatea.data

import com.example.compose.geniatea.presentation.userManagementSection.login.LoginScreenState
import com.example.compose.geniatea.presentation.settingsSection.changePass.ChangePassScreenState
import com.example.compose.geniatea.presentation.userManagementSection.preregister.PreRegisterScreenState
import com.example.compose.geniatea.presentation.userManagementSection.register.RegisterScreenState

val meProfile = LoginScreenState(
    username = "Laura123",
    password = "123456"
)

val meEmail = PreRegisterScreenState (
    email = ""
)

val meRegister = RegisterScreenState(
    // name = "Laura",
    username = "Laura_sdf",
    email = "Laura@gmail.com",
//    birthDate = "01/01/2000",
//    gender = "F",
    password = "123456",
    confirmPassword = "123456"
)

val meChangePass = ChangePassScreenState(
    currentPassword = "123456",
    newPassword = "123456",
    confirmNewPassword = "123456"
)