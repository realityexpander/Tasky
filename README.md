[<img src="https://user-images.githubusercontent.com/5157474/208806552-079ff4c4-9869-4921-9f6f-1f74d4211cdf.png" width=160 />](https://user-images.githubusercontent.com/5157474/208806552-079ff4c4-9869-4921-9f6f-1f74d4211cdf.png)


# Tasky
Task manager similar to a lite version of Google Calendar

* Manages schedule across time zones automatically
* Works offline & automatically syncs across multiple devices
* Invite other people to your events, and automatically sync changes.
* Alarms & Notifications for Agenda Items coming due
* Manage multiple user accounts
* Internationalized for English, Spanish and German


[<img src="https://user-images.githubusercontent.com/5157474/209284957-691e1ed7-1cdf-4e4f-8b87-9bb7fc213d38.png" width=200 />](https://user-images.githubusercontent.com/5157474/209284957-691e1ed7-1cdf-4e4f-8b87-9bb7fc213d38.png))
[<img src="https://user-images.githubusercontent.com/5157474/209285170-149a4306-0847-4382-bc07-66c3ea3eb3df.png" width=200 />](
https://user-images.githubusercontent.com/5157474/209285170-149a4306-0847-4382-bc07-66c3ea3eb3df.png)
[<img src="https://user-images.githubusercontent.com/5157474/209285183-49a62595-d840-4442-b403-67c8d0c1b027.png" width=200 />](
https://user-images.githubusercontent.com/5157474/209285183-49a62595-d840-4442-b403-67c8d0c1b027.png)
[<img src="https://user-images.githubusercontent.com/5157474/209285202-6c56cb56-83fd-421c-871b-929d422ebbb5.png" width=200 />](
https://user-images.githubusercontent.com/5157474/209285202-6c56cb56-83fd-421c-871b-929d422ebbb5.png)
[<img src="https://user-images.githubusercontent.com/5157474/209285234-f4cab9b6-46ca-40ac-9f4f-89db93771222.png" width=200 />](
https://user-images.githubusercontent.com/5157474/209285234-f4cab9b6-46ca-40ac-9f4f-89db93771222.png)
[<img src="https://user-images.githubusercontent.com/5157474/209286100-062e6dbd-6eea-4b51-a1b7-0d9aa5b86ea9.png" width=200 />](https://user-images.githubusercontent.com/5157474/209286100-062e6dbd-6eea-4b51-a1b7-0d9aa5b86ea9.png)
[<img src="https://user-images.githubusercontent.com/5157474/209300668-34279c57-f307-4579-82aa-e59e5088e3d7.png" width=200 />](https://user-images.githubusercontent.com/5157474/209300668-34279c57-f307-4579-82aa-e59e5088e3d7.png)


## Screen Flow
![image](https://user-images.githubusercontent.com/5157474/209296748-7d910239-84f0-4a58-aff7-e89577e55481.png)



## 🛠 Built With

- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android
  development.
- [Compose](https://developer.android.com/jetpack/compose) - Jetpack Compose is Android’s
  modern toolkit for building native UI.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - A coroutine is a
  concurrency design pattern that you can use on Android to simplify code that executes
  asynchronously.
- [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous
  version of a Sequence, a type of collection whose values are lazily produced.
- [Proto DataStore](https://developer.android.com/topic/libraries/architecture/datastore) -
  Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed
  objects with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data
  asynchronously, consistently, and transactionally.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) -
  Collection of libraries that help you design robust, testable, and maintainable apps.
    - [Stateflow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - StateFlow is
      a state-holder observable flow that emits the current and new state updates to its collectors.
    - [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous
      version of a Sequence, a type of collection whose values are lazily produced.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores
      UI-related data that isn"t destroyed on UI changes.
    - [Compose Destinations Navigation](https://developer.android.com/jetpack/compose/navigation) - 
      Simplified and type-safe navigation for Compose.
    - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Jetpack
      DataStore is a data storage solution that allows you to store key-value pairs or typed objects
      with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data asynchronously,
      consistently, and transactionally.
    - [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - 
      Schedule automatically repeating or guarantee one-time background processing.
    - [Room Database](https://developer.android.com/topic/libraries/architecture/room) -
      SQL-based database.
- [Material Components for Android](https://github.com/material-components/material-components-android)
    - Modular and customizable Material Design UI components for Android.
- [Accompanist](https://github.com/google/accompanist)
    - A collection of extension libraries for Jetpack Compose.
- [Coil]()
    - Image loading library for Compose.
<br />

## Architecture Overview
[<img src="https://user-images.githubusercontent.com/5157474/209253082-260178ef-73ef-4d31-bb85-0a7c07fc1b95.png"/>](https://excalidraw.com/#json=I9k6Db_Hm9x3cKzjrA0cH,V-Tt-3ypjv845qR10H8pdA)
<!-- original document
https://excalidraw.com/#json=XRhY4HYn5rx3vldB1J4oo,3yGmcP4YsL8lNDN_XUO9gA
-->

## Architecture Data Flow
<!--
Code for chart:
https://mermaid.live/edit#pako:eNptkk1LxDAQhv9KCJSC7CJ47KFQ3IuHRVHwYqRMm9ENbZIlne5SSv-7abqaLW5Oyfs-88FMRl5biTzjSTIqoyhjY0oH1JhmqQTXpNOUJMJ0BIQ7Bd8O9Pb0IAzzp6hJnRQN949WH20HVYtsu83Zu8Lz3mdtF-zvGcxX9Kgi64Zb7o2UCxbD1iWYMAsQOrzGxkWfz8fdZ4jaFc__xeLlKYqeCKKzVpcSCCroMNqeDbZBOlvXRGPFBwQNKVLYla2toY3kJdQz0NPh6pmzWbgF5oyga4ZSVtH9Vda1HGpLl34nvuEanQYl_XrDNAQPqxU889d5u4ILM3O-sn0bTM0zcj1ueH-Ucd88-4K28yrKebL75b-EbzP9AH52tNQ
-->

[![](https://mermaid.ink/img/pako:eNptkk1LxDAQhv9KCJSC7CJ47KFQ3IuHRVHwYqRMm9ENbZIlne5SSv-7abqaLW5Oyfs-88FMRl5biTzjSTIqoyhjY0oH1JhmqQTXpNOUJMJ0BIQ7Bd8O9Pb0IAzzp6hJnRQN949WH20HVYtsu83Zu8Lz3mdtF-zvGcxX9Kgi64Zb7o2UCxbD1iWYMAsQOrzGxkWfz8fdZ4jaFc__xeLlKYqeCKKzVpcSCCroMNqeDbZBOlvXRGPFBwQNKVLYla2toY3kJdQz0NPh6pmzWbgF5oyga4ZSVtH9Vda1HGpLl34nvuEanQYl_XrDNAQPqxU889d5u4ILM3O-sn0bTM0zcj1ueH-Ucd88-4K28yrKebL75b-EbzP9AH52tNQ?type=png)](https://mermaid.live/edit#pako:eNptkk1LxDAQhv9KCJSC7CJ47KFQ3IuHRVHwYqRMm9ENbZIlne5SSv-7abqaLW5Oyfs-88FMRl5biTzjSTIqoyhjY0oH1JhmqQTXpNOUJMJ0BIQ7Bd8O9Pb0IAzzp6hJnRQN949WH20HVYtsu83Zu8Lz3mdtF-zvGcxX9Kgi64Zb7o2UCxbD1iWYMAsQOrzGxkWfz8fdZ4jaFc__xeLlKYqeCKKzVpcSCCroMNqeDbZBOlvXRGPFBwQNKVLYla2toY3kJdQz0NPh6pmzWbgF5oyga4ZSVtH9Vda1HGpLl34nvuEanQYl_XrDNAQPqxU889d5u4ILM3O-sn0bTM0zcj1ueH-Ucd88-4K28yrKebL75b-EbzP9AH52tNQ)

## Entity Relationship

<!--
Code for chart:
https://mermaid.live/edit#pako:eNp9UsFqwzAM_RVjCDms_QFTChnZobDBWNubLyZWW9FYDrYyGFn-fa5TSkez-SCL954ky7xBNt6CVLIoBiRkJYaST-CgVKU14VyOY1FoaloTY43mGIzTJNKpjkDWbBicWH0vl-IDHJKFMM_uTDzPMy-fQPxAKfG0329qgXaOqg3DDh2IkIdWD_VJs-WAdBSM3MLftIXYBOwYPWmaZHnV2zrDBIq7oZzChI73FZcV_1Un9Nn7FgwJjLWnuSb5N2a6HIJ3M739DXvFyKuKOa0IsBbmmsXfgveTZx_Xosv3df4lyIV0EJxBm6yQ52uZbaClSunFCVpqGpPO9Oy3X9RIxaGHhew7m95z9YZUB9PGhIJF9uFt8la22PgDjDHFzQ
-->

[![](https://mermaid.ink/img/pako:eNp9U2FrwjAQ_SshUDqZ_oEigqNjCA7G1G_5EpqbHjZJSc7B6PrfF9OqVev6IQ3vvdzl3eVqXlgFPONJUqNBylid0g40pFmqpNunTZMkwhSl9D5HuXVSC8PCN9-CUXJBoNn0dzJhn6DRKHDD7Fr6_TDz-g2G7qiMPW82i5yhGqJySbBGDczFpPO780GzIodmywiphMe0Al84rAitEaaVRatnO3ULsl5SCkuLNv0TR4v_qgP6Ym0J0jD0uTVDQWI1BqJ8OasHYtsztkRP0zlRsAgwY7Lb-WvBx86S9TNWxf9V_pi5bUlU3aGn4OKmUlHNznfuFdASPKEas43D0S29tIUsO7YcDVTilK4f-fpN3FT0zYaG9pgHjyRyXfeNvPQmoK2T6uK-4WOuwWmJKkxIvIjgcToEz8L2OCCCC3PUyQPZ1Y8peEbuAGN-qFS4QDcyPPuSpQ8oKCTr3tuRi5PX_AEIWRhw?type=png)](https://mermaid.live/edit#pako:eNp9U2FrwjAQ_SshUDqZ_oEigqNjCA7G1G_5EpqbHjZJSc7B6PrfF9OqVev6IQ3vvdzl3eVqXlgFPONJUqNBylid0g40pFmqpNunTZMkwhSl9D5HuXVSC8PCN9-CUXJBoNn0dzJhn6DRKHDD7Fr6_TDz-g2G7qiMPW82i5yhGqJySbBGDczFpPO780GzIodmywiphMe0Al84rAitEaaVRatnO3ULsl5SCkuLNv0TR4v_qgP6Ym0J0jD0uTVDQWI1BqJ8OasHYtsztkRP0zlRsAgwY7Lb-WvBx86S9TNWxf9V_pi5bUlU3aGn4OKmUlHNznfuFdASPKEas43D0S29tIUsO7YcDVTilK4f-fpN3FT0zYaG9pgHjyRyXfeNvPQmoK2T6uK-4WOuwWmJKkxIvIjgcToEz8L2OCCCC3PUyQPZ1Y8peEbuAGN-qFS4QDcyPPuSpQ8oKCTr3tuRi5PX_AEIWRhw)

## Authentication Flow State Diagram
To simulate, click on the diagram.
[<img src="https://user-images.githubusercontent.com/5157474/209244377-1b7d7e1c-a46b-416c-9dbf-7a77d46036b6.png"/>](https://stately.ai/registry/editor/f97c13d8-df51-4f0a-a750-873f74f74345?machineId=91f75b44-fe82-45aa-8434-d7a839a4ab0f)

## Offline Cache Strategy State Diagram
To simulate, click on the diagram.

[<img src="https://user-images.githubusercontent.com/5157474/209246393-5ec4ca55-0ac2-41da-a1dd-ee2354451664.png"/>](https://stately.ai/registry/editor/f97c13d8-df51-4f0a-a750-873f74f74345?machineId=5ad64523-72e6-4c45-8be6-d5249be291e9)

