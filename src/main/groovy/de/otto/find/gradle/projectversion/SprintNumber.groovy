package de.otto.find.gradle.projectversion

import java.time.Duration
import java.time.Instant

class SprintNumber {

    Instant sprintStart = Instant.parse("2011-09-28T23:00:00Z")
    Duration sprintLength = Duration.ofDays(14)
    int sprintStartNumber = 1

    int sprintNumber() {
        (int) (Duration.between(sprintStart, Instant.now()).toDays() / sprintLength.toDays()) + sprintStartNumber
    }
}

