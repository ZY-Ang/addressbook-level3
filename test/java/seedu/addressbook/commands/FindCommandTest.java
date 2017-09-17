package seedu.addressbook.commands;

import org.junit.Test;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.util.TypicalPersons;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FindCommandTest {

    private final AddressBook addressBook = new TypicalPersons().getTypicalAddressBook();
    private final TypicalPersons td = new TypicalPersons();

    @Test
    public void execute() throws IllegalValueException {
        //same word, same case: matched
        assertFindCommandBehavior(new String[]{"Amy"}, Arrays.asList(td.amy));

        //same word, different case: matched
        assertFindCommandBehavior(new String[]{"aMy"}, Arrays.asList(td.amy));

        //partial word: matched
        assertFindCommandBehavior(new String[]{"my"}, Arrays.asList(td.amy));

        //multiple words: matched
        assertFindCommandBehavior(new String[]{"Amy", "Bill", "Candy", "Destiny"},
                Arrays.asList(td.amy, td.bill, td.candy));

        //repeated keywords: matched
        assertFindCommandBehavior(new String[]{"Amy", "Amy"}, Arrays.asList(td.amy));

        //Keyword matching a word in address: not matched
        assertFindCommandBehavior(new String[]{"Clementi"}, Collections.emptyList());
        
        //Keyword matching a tag: matched
        assertFindCommandBehavior(new String[]{}, new String[]{"Test"}, Arrays.asList(td.dan));
        
        //Keywords matching tag or name: matched
        assertFindCommandBehavior(new String[]{"Candy"}, new String[]{"Test"}, Arrays.asList(td.candy, td.dan));
        
        //check for repeated name
        assertFindCommandBehavior(new String[]{"Dan"}, new String[]{"Test"}, Arrays.asList(td.dan));
    }

    /**
     * Executes the find command for the given keywords and verifies
     * the result matches the persons in the expectedPersonList exactly.
     */
    private void assertFindCommandBehavior(String[] keywords, List<ReadOnlyPerson> expectedPersonList) {
        FindCommand command = createFindCommand(keywords, new String[]{});
        CommandResult result = command.execute();

        assertEquals(Command.getMessageForPersonListShownSummary(expectedPersonList), result.feedbackToUser);
    }

    private void assertFindCommandBehavior(String[] keywords, String[] tags, List<ReadOnlyPerson> expectedPersonList) {
        FindCommand command = createFindCommand(keywords, tags);
        CommandResult result = command.execute();

        assertEquals(Command.getMessageForPersonListShownSummary(expectedPersonList), result.feedbackToUser);
    }

    private FindCommand createFindCommand(String[] keywords, String[] tagwords) {
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        FindCommand command = new FindCommand(keywordSet);
        command.setData(addressBook, Collections.emptyList());
        return command;
    }

}
