package com.example.trackr.shared.data;

import com.example.trackr.shared.db.tables.Avatar;
import com.example.trackr.shared.db.tables.Tag;
import com.example.trackr.shared.db.tables.TagColor;
import com.example.trackr.shared.db.tables.TaskStatus;
import com.example.trackr.shared.db.tables.TaskTag;
import com.example.trackr.shared.db.tables.User;
import com.example.trackr.shared.db.tables.UserTask;
import com.example.trackr.shared.db.tables.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


/**
 * @author Rameel Hassan
 * Created 02/03/2023 at 12:12 PM
 */

public class SeedData {
    public static final List<User> Users = List.of(
            new User(1L, "Daring Dove", Avatar.DARING_DOVE),
            new  User(2L,  "Likeable Lark",  Avatar.LIKEABLE_LARK),
            new User( 3L, "Peaceful Puffin", Avatar.PEACEFUL_PUFFIN)
    );

    public static final List<Tag> Tags = List.of(
           new Tag( 1L,  "2.3 release",TagColor.BLUE),
           new Tag( 2L,  "2.4 release", TagColor.RED),
           new Tag( 3L,  "a11y", TagColor.GREEN),
           new Tag( 4L,  "UI/UX", TagColor.PURPLE),
           new Tag( 5L,  "eng",  TagColor.TEAL),
           new Tag( 6L, "VisD",  TagColor.YELLOW)
    );

    public static final List<TaskTag> TaskTags = List.of(
            new TaskTag(1L, 2L),
            new TaskTag(1L, 4L),

            new TaskTag(2L, 1L),

            new TaskTag(3L, 4L),
            new TaskTag(3L, 5L),

            new TaskTag(4L, 2L),
            new TaskTag(4L, 5L),

            new TaskTag(5L, 1L),
            new TaskTag(5L, 4L),

            new TaskTag(6L, 1L),
            new TaskTag(6L, 5L),
            new TaskTag(6L, 6L),

            new TaskTag(7L, 2L),
            new TaskTag(7L, 3L),

            new TaskTag(8L, 1L),

            new TaskTag(9L, 2L),

            new TaskTag(10L, 1L),
            new TaskTag(10L, 4L),

            new TaskTag(11L, 2L),

            new TaskTag(12L, 4L),
            new TaskTag(12L, 6L),

            new TaskTag(13L, 1L),
            new TaskTag(13L, 3L),
            new TaskTag(13L, 5L),
            new TaskTag(13L, 6L),

            new TaskTag(14L, 2L),

            new TaskTag(15L, 1L),
            new TaskTag(15L, 6L)
    );

    public static final List<UserTask> UserTasks = List.of(
            new  UserTask( 1L, 1L)
    );


    public static final List<Task> Tasks = List.of(
            Task.builder()
                    .setId(1L)
                    .setTitle("New mocks for tablet")
                    .setDescription ("For our mobile apps, we currently only target phones. We need to " +
                            "start moving towards supporting tablets as well, and we should kick off the " +
                            "process of getting the mocks together")
                    .setStatus(TaskStatus.NOT_STARTED)
                    .setOwnerId (1L)
                    .setCreatorId(2L)
                    .setOrderInCategory(1).build(),
            Task.builder()
                    .setId (2L)
                    .setTitle ("Move the owner icon")
                    .setDescription ("Our UX research has shown that users prefer that, and even though " +
                            "all us remain split on this, let’s go ahead and move the owner icon in the " +
                            "way specified in the mocks.")
                    .setStatus(TaskStatus.NOT_STARTED)
                    .setOwnerId(1L)
                    .setCreatorId(3L)
                    .setDueAt(Instant.now().minus( Duration.ofDays(1)))
                    .setOrderInCategory(2)
                    .build(),
            Task.builder()
                    .setId(3L)
                    .setTitle("Allow optional guests")
                    .setDescription("Feedback from product: people find our app useful for scheduling " +
                            "things, but they really, really want to have a way to have optional " +
                            "invites. Right now, anyone who receives a calendar invite has no way of " +
                            "knowing how important (or unimportant) their presence is at the meeting. " +
                            "Alternatively, we could set up some kind of numerical 'important to attend' " +
                            "system (with 5 as most important, and 1 as totally optional), but this " +
                            "might be needlessly complex. Adding a simple “optional” bit in db might " +
                            "be all we need (plus the necessary UI changes). \n")
                    .setStatus(TaskStatus.NOT_STARTED)
                    .setOwnerId(2L)
                    .setCreatorId(1L)
                    .setCreatedAt(Instant.now().minus(Duration.ofDays(1)))
                    .setDueAt(Instant.now().minus(Duration.ofDays(2)))
                    .setOrderInCategory(3)
                    .build(),
            Task.builder()
                   .setId(4L)
                    .setTitle("Suggest meeting times")
                    .setDescription("I’m finding that I have to look at the invitees’ calendars and work " +
                            "out the optimal time for a meeting. Surely our app can work this out and " +
                            "just offer a few times as the guest list is created? I think this could save " +
                            "our users a good amount of time. ")
                    .setStatus(TaskStatus.NOT_STARTED)
                    .setOwnerId(3L)
                    .setCreatorId(2L)
                    .setDueAt(Instant.now().plus( Duration.ofDays(5)))
                    .setOrderInCategory(4)
                    .build(),
            Task.builder()
                    .setId(5L)
                    .setTitle("Enable share feature")
                    .setDescription("We’ve talked about this feature for a while and it’s fairly easy to " +
                            "implement (and the mocks exist). Let’s get it in the next release \n")
                    .setStatus(TaskStatus.NOT_STARTED)
                    .setOwnerId(1L)
                    .setCreatorId(3L)
                    .setDueAt(Instant.now().minus(Duration.ofDays(10)))
                    .setOrderInCategory(5)
                    .build(),

            Task.builder()
                   .setId(6L)
                   .setTitle("Support display modes")
                    .setDescription("Default to the weekly view in desktop and daily view in mobile , but " +
                            "let users switch between modes seamlessly")
                  .setStatus(TaskStatus.IN_PROGRESS)
                  .setOwnerId(2L)
                  .setCreatorId(2L)
                  .setDueAt(Instant.now().plus(Duration.ofDays(2)))
                  .setOrderInCategory(1)
           .build(),
            Task.builder()
                   .setId(7L)
                   .setTitle("Let users set bg color")
                    .setDescription("We may want to present a finite palette of colors from which the user " +
                            "chooses the default; if there are a large number of options, we’ll probably " +
                            "find ourselves dealing with poor contrast between foreground and background")
                  .setStatus(TaskStatus.IN_PROGRESS)
                  .setOwnerId(1L)
                  .setCreatorId(2L)
                  .setDueAt(Instant.now().plus(Duration.ofDays(12)))
                  .setOrderInCategory(2)
           .build(),
            Task.builder()
                   .setId(8L)
                   .setTitle("Implement smart sync")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(1L)
                  .setCreatorId(1L)
                  .setDueAt(Instant.now().plus( Duration.ofDays(22)))
                  .setOrderInCategory(1)
           .build(),
            Task.builder()
                   .setId(9L)
                   .setTitle("Smartwatch UI design")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(3L)
                  .setCreatorId(3L)
                  .setDueAt(Instant.now().plus(Duration.ofDays(14)))
                  .setOrderInCategory(2)
           .build(),
            Task.builder()
                   .setId(10L)
                   .setTitle("New content for notifications")
                   .setDescription("Notif types: event coming up in [x] mins; event right now; time to " +
                            "leave; event changed / cancelled; invited to new event")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(3L)
                  .setCreatorId(2L)
                  .setDueAt(Instant.now().minus(Duration.ofDays(9)))
                  .setOrderInCategory(3)
           .build(),
            Task.builder()
                   .setId(11L)
                   .setTitle("Create default illustrations for event types")
                    .setDescription("Event types: coffee, lunch, gym, sports, travel, doctors appointment, " +
                            "game day, presentation, movie, theatre, class")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(1L)
                  .setCreatorId(3L)
                  .setDueAt(Instant.now().plus(Duration.ofDays(6)))
                  .setOrderInCategory(4)
           .build(),
            Task.builder()
                   .setId(12L)
                   .setTitle("Auto-decline holiday events")
                    .setDescription("User setting that automatically declines new meetings if they’re set " +
                            "on a holiday")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(2L)
                  .setCreatorId(3L)
                  .setDueAt(Instant.now().minus(Duration.ofDays(3)))
                  .setOrderInCategory(5)
           .build(),
            Task.builder()
                   .setId(13L)
                   .setTitle("Holiday conflict warning")
                    .setDescription("Pop up dialog warning user if they try to schedule a meeting on at " +
                            "least one of the participants’ holiday, depending on user profile location")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(1L)
                  .setCreatorId(1L)
                  .setDueAt(Instant.now().plus(Duration.ofDays(1)))
                  .setOrderInCategory(1)
                    .setArchived(true)
           .build(),
            Task.builder()
                   .setId(14L)
                   .setTitle("Prepopulate holidays")
                   .setDescription("Show national holidays (listed at top of each day’s schedule) " +
                            "directly in calendar view based on user profile location. Let users opt out " +
                            "of this if they want")
                  .setStatus(TaskStatus.COMPLETED)
                  .setOwnerId(2L)
                  .setCreatorId(3L)
                  .setDueAt(Instant.now().plus(Duration.ofDays(8)))
                  .setOrderInCategory(2)
                    .setArchived(true)
           .build(),
            Task.builder()
                   .setId(15L)
                   .setTitle("Holiday-specific illustrations")
                   .setDescription("Create illustrations that match each holiday. Prioritize for tier 1 " +
                            "regions first")
                    .setStatus(TaskStatus.COMPLETED)
                    .setOwnerId(3L)
                    .setCreatorId(3L)
                    .setOrderInCategory(3)
                    .setArchived(true)
                    .build()
    );
    
    
}
