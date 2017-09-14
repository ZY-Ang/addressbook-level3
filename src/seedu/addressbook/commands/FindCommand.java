package seedu.addressbook.commands;

import seedu.addressbook.common.Utils;
import seedu.addressbook.data.person.ReadOnlyPerson;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.addressbook.data.tag.UniqueTagList;

/**
 * Finds and lists all persons in address book whose name or tag contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names start with "
            + "the specified keywords (case-sensitive) or contains tag(s) specified and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS] t/TAG_NAME \n"
            + "Example: " + COMMAND_WORD + " alice bob charlie t/tag";

    private static final int FIND_DISTANCE_TOLERANCE = 3;

    private final Set<String> keyWords;

    public FindCommand(Set<String> keyWords) {
        this.keyWords = keyWords;
    }

    /**
     * Returns copy of keywords in this command.
     */
    public Set<String> getKeywords() {
        return new HashSet<>(keyWords);
    }

    @Override
    public CommandResult execute() {
        final List<ReadOnlyPerson> personsFound = getPersonsWithNameContainingAnyKeyword(keyWords);
        return new CommandResult(getMessageForPersonListShownSummary(personsFound), personsFound);
    }

    /**
     * Retrieves all persons in the address book whose names contain some of the specified keywords,
     * within a levenshtein distance of {@link #FIND_DISTANCE_TOLERANCE}.
     * Retrieves all persons in the address book whose names start with specified keywords or who contain specified tag.
     *
     * @param keywords for searching names
     * @return list of persons found
     */
    private List<ReadOnlyPerson> getPersonsWithNameContainingAnyKeyword(Set<String> keywords) {
        final List<ReadOnlyPerson> matchedPersons = new ArrayList<>();

        for (ReadOnlyPerson person : addressBook.getAllPersons()) {
            boolean isPersonAdded = false;
            final UniqueTagList personTags = person.getTags();
            
            // check for matching name using lehvenstein distance
            if (doesPersonsNameContainAnyKeyword(person, keywords)) {
                matchedPersons.add(person);
                isPersonAdded = true;
            }

            // check for tag if person has not been added
            if (!isPersonAdded) {
                for (String tag : tagKeywords) {
                    for (Tag personTag : personTags) {
                        if (personTag.tagName.equals(tag)) {
                            if (!isPersonAdded) {
                                matchedPersons.add(person);
                                isPersonAdded = true;
                            }
                        }
                    }
                }
            }

        }

        return matchedPersons;
    }

    /**
     * Returns whether a person's name contains some of the specified keywords, within a levenshtein
     * distance of {@link #FIND_DISTANCE_TOLERANCE}.
     *
     * @param person   to search
     * @param keywords for searching
     * @return whether person's name contains keywords
     */
    private boolean doesPersonsNameContainAnyKeyword(ReadOnlyPerson person, Set<String> keywords) {
        final Set<String> wordsInName = new HashSet<>(person.getName().getWordsInName());
        for (String a : wordsInName) {
            for (String b : keywords) {
                if (Utils.levenshteinDistance(a, b) < FIND_DISTANCE_TOLERANCE) {
                    return true;
                }
            }
        }
        return false;
    }

}
