+-------------------------------------------------------+
|                  Create User                         |
+-------------------------------------------------------+
|
v
+-----------------------------------+                  
| Is name and email provided?      |------------------- No --> Throw error: "Name and email are required."
| (Check if null or empty)         |
+-----------------------------------+
| Yes
v
+-----------------------------------+
| Is email valid?                   |------------------ No --> Throw error: "Invalid email address."
| (Validate email syntax)          |
+-----------------------------------+
|
v
+-----------------------------------+
| Is SupportedTimeZone provided?            |----------------- No --> Set default timezone (IST).
| (Check if null)                  |
+-----------------------------------+
|
v
+-----------------------------------+
| Is Profile provided?             |---------------- No --> Set default profile as "PUBLIC".
| (Check if null)                  |
+-----------------------------------+
|
v
+-----------------------------------+
| Create UserEntity                |
| (UserEntityTranslator.toEntity)  |
+-----------------------------------+
|
v
+-----------------------------------+
| Save UserEntity to DB            |
| (userRepository.save)            |
+-----------------------------------+
|
v
+-----------------------------------+
| Return created UserEntity        |
+-----------------------------------+



+----------------------------------------------------------+
|                Get User By Email                        |
+----------------------------------------------------------+
|
v
+-----------------------------------+
| Is email valid?                   |------------------- No --> Throw error: "Invalid email address."
| (Validate email syntax)          |
+-----------------------------------+
|
v
+-----------------------------------+
| Find User by Email and Check     |
| if not deleted                    |
+-----------------------------------+
|
v
+-----------------------------------+
| User found?                       |------------------- No --> Return null
| (userRepository.findByEmail)      |
+-----------------------------------+
|
v
+-----------------------------------+
| Return UserEntity                |
+-----------------------------------+



+----------------------------------------------------------+
|                Update User Information                   |
+----------------------------------------------------------+
|
v
+-----------------------------------+
| Find user by email               |
| (userRepository.findByEmail)     |
+-----------------------------------+
|
v
+-----------------------------------+
| User found?                      |------------------ No --> Throw error: "User not found."
| (userRepository.findByEmail)     |
+-----------------------------------+
|
v
+-----------------------------------+
| Update Fields if Provided        |
| (Name, Profile, SupportedTimeZone)        |
+-----------------------------------+
|
v
+-----------------------------------+
| Save Updated UserEntity          |
| (userRepository.save)            |
+-----------------------------------+
|
v
+-----------------------------------+
| Return Updated UserEntity        |
+-----------------------------------+



+----------------------------------------------------------+
|                    Delete User                           |
+----------------------------------------------------------+
|
v
+-----------------------------------+
| Is email valid?                   |------------------- No --> Throw error: "Invalid email address."
| (Validate email syntax)          |
+-----------------------------------+
|
v
+-----------------------------------+
| Soft delete user by email        |
| (userRepository.softDeleteByEmail)|
+-----------------------------------+
|
v
+-----------------------------------+
| End Delete User                  |
+-----------------------------------+





+--------------------------------------------------+
|                  Create Schedule                |
+--------------------------------------------------+
|
v
+-------------------+
| Check if available |
| times are empty    |-------------------- No --> Throw error: "User available times are required."
+-------------------+                               
|                                          
v                                          
+-------------------------------+            
| User email exists?             |------------------- No --> Throw error: "User email is not valid"
| (isValidUser)                  |                   
+-------------------------------+                   
|
v
+----------------------------+
| Validate available times   |------------------- No --> Throw error: "Invalid time slots"
| (validateAvailableTimes)    |                           
+----------------------------+                           
|                                        
v                                        
+--------------------------------------+          
| Create ScheduleEntity               |
| (ScheduleEntityTranslator.toEntity) |
+--------------------------------------+
|
v
+--------------------------+
| Save schedule entity      |
| (scheduleRepository.save) |
+--------------------------+
|
v
+-------------------------------+
| Return created schedule       |
+-------------------------------+



+--------------------------------------------------+
|                    Get Schedule                 |
+--------------------------------------------------+
|
v
+-------------------+
| User email exists? |
| (isValidUser)      |------------------- No --> Throw error: "User email is not valid"
+-------------------+                               
|                                          
v                                          
+-------------------------------+            
| Schedule exists for email/date |           
| (scheduleRepository.findByEmailAndDate)    
+-------------------------------+                   
|
v
+---------------------------+
| Return schedule entity    |
+---------------------------+


+--------------------------------------------------+
|                    Update Schedule              |
+--------------------------------------------------+
|
v
+-------------------+
| Check if available |
| times are empty    |-------------------- No --> Throw error: "User available times are required."
+-------------------+                               
|                                          
v                                          
+-------------------------------+            
| User email exists?             |           ------------------- No --> Throw error: "User email is not valid"
| (isValidUser)                  |                   
+-------------------------------+                   
|
v
+----------------------------+
| Validate available times   |------------------- No --> Throw error: "Invalid time slots"
| (validateAvailableTimes)    |                           
+----------------------------+                           
|                                        
v                                        
+--------------------------------------+          
| Update schedule available times    |
| (scheduleRepository.updateAvailableTimes) |
+--------------------------------------+
|
v
+----------------------------------------+
| No rows updated?                      |------------------- No --> Throw error: "No schedule found"
| (Check if schedule exists for email)  |                       
+----------------------------------------+                       
|
v
+-------------------------------------+
| Return updated schedule entity     |
| (scheduleRepository.findByEmailAndDate) |
+-------------------------------------+



+--------------------------------------------------+
|                    Delete Schedule              |
+--------------------------------------------------+
|
v
+-------------------+
| User email exists? |
| (isValidUser)      |------------------- No --> Throw error: "User email is not valid"
+-------------------+                               
|                                          
v                                          
+-------------------------------+            
| Soft delete schedule          |           ------------------- No --> Throw error: "User email is not valid"
| (scheduleRepository.softDelete) |           
+-------------------------------+   




+----------------------------------------+
|         Create Meeting                 |
+----------------------------------------+
|
v
+---------------------------------------------+
| Is the user valid?                         |
| (isValidUser)                              |
+---------------------------------------------+
|
v
+-------------------------+------------------+
| NO --> Throw error: "User email is not valid."  |
+-------------------------+                      |
|                                        v
v                             +---------------------------------------+
+-------------------------------------+  | Validate Invitees                   |
| Validate Invitees (validateInvitees) |  | (check if invitees exist)           |
+-------------------------------------+  +---------------------------------------+
|
v
+----------------------------------------+
| Is the time interval valid?           |
| (isValidTimeInterval)                 |
+----------------------------------------+
|
v
+-------------------------+------------------+
| NO --> Throw error: "Meeting time slot must have a minimum duration of 15 minutes."|
+-------------------------+                      |
|                                        v
v                             +----------------------------------------+
+-----------------------------------------+ | Ensure Required Invitees Availability |
| Ensure Required Invitees Availability   | | (ensure invitees availability)       |
| (ensureRequiredInviteesAvailability)    | +----------------------------------------+
+-----------------------------------------+
|
v
+------------------------------------------------------+
| Convert meetingDto to MeetingEntity (MeetingEntityTranslator.toEntity) |
+------------------------------------------------------+
|
v
+---------------------------------------------------+
| Save the created MeetingEntity                   |
| (meetingRepository.save)                          |
+---------------------------------------------------+
|
v
+--------------------------------------------------+
| Save User Meeting Entities for invitees         |
| (saveUserMeetings)                              |
+--------------------------------------------------+
|
v
+-------------------------------------------+
| Return created MeetingEntity              |
+-------------------------------------------+



+----------------------------------------+
|         Get Meeting                   |
+----------------------------------------+
|
v
+---------------------------------------------+
| Find meeting by meetingId                 |
| (meetingRepository.findById)              |
+---------------------------------------------+
|
v
+---------------------------+-----------------+
| Meeting found?            |                 |
| (if meeting exists)       |---------------- NO --> Return null or throw error
+---------------------------+                 |
|                                      v
v                             +---------------------------------------+
+-------------------------------+       | Throw error: "Meeting not found."   |
| Return meeting entity         |       +---------------------------------------+
| (meetingRepository)           |
+-------------------------------+



+--------------------------------------------+
|         Update Meeting                    |
+--------------------------------------------+
|
v
+---------------------------------------------+
| Is the user valid?                         |
| (isValidUser)                              |
+---------------------------------------------+
|
v
+-------------------------+------------------+
| NO --> Throw error: "User email is not valid."  |
+-------------------------+                      |
|                                        v
v                             +---------------------------------------+
+-------------------------------------+  | Validate Invitees                   |
| Validate Invitees (validateInvitees) |  | (check if invitees exist)           |
+-------------------------------------+  +---------------------------------------+
|
v
+----------------------------------------+
| Is the time interval valid?           |
| (isValidTimeInterval)                 |
+----------------------------------------+
|
v
+-------------------------+------------------+
| NO --> Throw error: "Meeting time slot must have a minimum duration of 15 minutes."|
+-------------------------+                      |
|                                        v
v                             +----------------------------------------+
+-----------------------------------------+ | Ensure Required Invitees Availability |
| Ensure Required Invitees Availability   | | (ensure invitees availability)       |
| (ensureRequiredInviteesAvailability)    | +----------------------------------------+
+-----------------------------------------+
|
v
+------------------------------------------------------+
| Find existing meeting by meetingId                  |
| (meetingRepository.findById)                         |
+------------------------------------------------------+
|
v
+---------------------------+-----------------+
| Meeting found?            |                 |
| (if meeting exists)       |---------------- NO --> Throw error: "Meeting with ID not found"
+---------------------------+                 |
|                                      v
v                             +---------------------------------------+
+-------------------------------+       | Throw error: "Meeting with ID not found." |
| Update meeting details         |       +---------------------------------------+
| (update meetingEntity fields) |
+-------------------------------+
|
v
+---------------------------------------------------+
| Save updated MeetingEntity                        |
| (meetingRepository.save)                          |
+---------------------------------------------------+
|
v
+---------------------------------------------------+
| Update User Meeting Entities for new invitees    |
| (updateUserMeetings)                              |
+---------------------------------------------------+
|
v
+-------------------------------------------+
| Return updated MeetingEntity              |
+-------------------------------------------+



+-----------------------------------------------+
|         Save User Meetings                   |
+-----------------------------------------------+
|
v
+-----------------------------------------------+
| Add all required and optional invitees       |
| (invitees.addAll)                            |
+-----------------------------------------------+
|
v
+------------------------------------------------------+
| For each invitee, create UserMeetingEntity          |
| (userMeetingRepository.save)                        |
+------------------------------------------------------+



+---------------------------------------------------------------+
|         Update User Meetings                              |
+---------------------------------------------------------------+
|
v
+---------------------------------------------------+
| Find all UserMeetingEntities for the meeting     |
| (userMeetingRepository.findByMeetingId)          |
+---------------------------------------------------+
|
v
+---------------------------------------------+
| Get existing invitees from UserRepository   |
| (userRepository.findEmailsByIds)            |
+---------------------------------------------+
|
v
+---------------------------------------------+
| Get new invitees from meetingEntity         |
+---------------------------------------------+
|
v
+--------------------------------------------------+
| Find invitees to remove and delete from UserMeeting |
| (userMeetingRepository.deleteByUserIdAndMeetingId) |
+--------------------------------------------------+
|
v
+-----------------------------------------------+
|           End Update User Meetings            |
+-----------------------------------------------+





+--------------------------------------+
|         Get User Meetings           |
+--------------------------------------+
|
v
+-------------------------------+
| Find meeting IDs for userId    |
| (userMeetingRepository)        |
+-------------------------------+
|
v
+-------------------------------+
| Find meetings by meetingIds    |
| (meetingRepository.findByIdIn) |
+-------------------------------+
|
v
+-------------------------------+
| Add meetings where user is     |
| the creator                    |
| (meetingRepository.findByCreatorUserId) |
+-------------------------------+
|
v
+-------------------------------------+
| Return list of meetings            |
+-------------------------------------+



+-----------------------------------------------+
|         Get User Meeting By Id               |
+-----------------------------------------------+
|
v
+---------------------------------------------+
| Find meeting ID by userId and meetingId    |
| (userMeetingRepository)                    |
+---------------------------------------------+
|
v
+------------------------+
| Is meetingId found?     |                    
| (if meeting ID exists)  |------------------- NO --> Find meeting where user is creator
+------------------------+                           |
|                                          v
v                          +---------------------------------------+
+---------------------------+          | (meetingRepository.findByCreatorUserId) |
| Meeting exists for user    |          +---------------------------------------+
| (meetingRepository.findById)|                        |
+---------------------------+                        v
|                              +-------------------------------------+
v                              | Meeting found?                     |
+--------------------------+            | (yes, return meeting)             |
| Return meeting entity    |------------|                                     |
| (meetingRepository)      |            +-------------------------------------+
+--------------------------+                        |
|                                          v
v                                  +-------------------------+
+---------------------------------------------+     | Throw error: "No such  |
|         End Get User Meeting By Id          |     | meeting exists for this user"|
+---------------------------------------------+     +-------------------------+



+------------------------------------------------------------+
|          Update User Meeting Status                        |
+------------------------------------------------------------+
|
v
+------------------------------------------+
| Update user status in userMeetingRepo   |
| (userMeetingRepository.updateUserStatus) |
+------------------------------------------+
|
v
+------------------------------------------+
| Was update successful?                  |
| (check if rows affected == 0)           |
+------------------------------------------+
|
v
+-------------------------+                
| Update failed            |--- YES ---> Throw error: "No such meeting exists for this user"
| (throw error)            |                
+-------------------------+                
|                                    
v                                    
+--------------------------+
| Return meeting entity    |
| (meetingRepository)      |
+--------------------------+

