User Operations
1. Create User

    Check if name and email are provided:
        If No: Throw error: "Name and email are required."
    Validate email syntax:
        If No: Throw error: "Invalid email address."
    Check if SupportedTimeZone is provided:
        If No: Set default timezone to IST.
    Check if Profile is provided:
        If No: Set default profile to "PUBLIC".
    Create UserEntity using a translator.
    Save UserEntity to the database.
    Return the created UserEntity.

2. Get User By Email

    Validate email syntax:
        If No: Throw error: "Invalid email address."
    Find the user by email (ensure it is not soft-deleted).
    Check if user exists:
        If No: Return null.
    Return the UserEntity.

3. Update User Information

    Find the user by email.
        If No: Throw error: "User not found."
    Update fields (Name, Profile, SupportedTimeZone) if provided.
    Save the updated UserEntity.
    Return the updated UserEntity.

4. Delete User

    Validate email syntax:
        If No: Throw error: "Invalid email address."
    Perform a soft delete of the user by email.

Schedule Operations
1. Create Schedule

    Check if available times are empty:
        If Yes: Throw error: "User available times are required."
    Validate user email:
        If No: Throw error: "User email is not valid."
    Validate available times:
        If No: Throw error: "Invalid time slots."
    Create ScheduleEntity using a translator.
    Save ScheduleEntity to the database.
    Return the created ScheduleEntity.

2. Get Schedule

    Validate user email:
        If No: Throw error: "User email is not valid."
    Check if a schedule exists for the email and date.
    Return the ScheduleEntity if found.

3. Update Schedule

    Check if available times are empty:
        If Yes: Throw error: "User available times are required."
    Validate user email:
        If No: Throw error: "User email is not valid."
    Validate available times:
        If No: Throw error: "Invalid time slots."
    Update available times in the schedule.
        If no rows are updated: Throw error: "No schedule found."
    Return the updated ScheduleEntity.

4. Delete Schedule

    Validate user email:
        If No: Throw error: "User email is not valid."
    Perform a soft delete of the schedule.

Meeting Operations
1. Create Meeting

    Validate user email:
        If No: Throw error: "User email is not valid."
    Validate invitees.
    Validate the time interval:
        If Invalid: Throw error: "Meeting time slot must have a minimum duration of 15 minutes."
    Ensure required invitees' availability.
    Convert meetingDto to MeetingEntity.
    Save the MeetingEntity to the database.
    Save user-meeting entities for invitees.
    Return the created MeetingEntity.

2. Get Meeting

    Find meeting by meeting ID.
        If not found: Return null or throw error.
    Return the MeetingEntity.

3. Update Meeting

    Validate user email:
        If No: Throw error: "User email is not valid."
    Validate invitees.
    Validate the time interval:
        If Invalid: Throw error: "Meeting time slot must have a minimum duration of 15 minutes."
    Ensure required invitees' availability.
    Find the existing meeting by ID:
        If not found: Throw error: "Meeting with ID not found."
    Update meeting details.
    Save the updated MeetingEntity.
    Update user-meeting entities for new invitees.
    Return the updated MeetingEntity.

4. Delete Meeting

    Validate user email:
        If No: Throw error: "User email is not valid."
    Perform a soft delete of the meeting.


User Meeting Operations
1. Get UserMeetings
        Purpose: Retrieve all meetings for a user (as invitee or creator).
        Steps:
            Validate email syntax.
            Check if user exists; throw error if not.
            Fetch meeting IDs (invitee) and meetings (creator).
            Return combined results.

2.  Get UserMeeting
        Purpose: Get specific meeting details for a user.
        Steps:
            Validate email and check user existence.
            Find meeting by ID (invitee/creator); throw error if not associated.
            Return meeting entity.

3.  Patch UserMeetingStatus
        Purpose: Update user’s meeting status (accepted/rejected).
        Steps:
            Validate email and check user existence.
            Fetch meeting and schedule details.
            ACCEPTED: Validate availability; remove conflicts.
            REJECTED: Restore and merge availability if previously accepted.
            Update database and return updated meeting.

    removeOverlappingTime(MeetingEntity meetingEntity, ScheduleEntity scheduleEntity)
        Purpose: Remove overlapping slots from schedule.
        Steps: Convert to UTC, split conflicting slots, retain non-overlapping slots.

    addBackCommonTimeAndMerge(MeetingEntity meetingEntity, ScheduleEntity scheduleEntity)
        Purpose: Restore and merge availability after rejection.
        Steps: Normalize times, add back meeting time, merge overlapping slots.

    mergeOverlappingTimeSlots(List<AvailableTimes>, String scheduleTimezone)
        Purpose: Merge overlapping or adjacent time slots.
        Steps: Sort slots, iterate to merge conflicts, return merged list.