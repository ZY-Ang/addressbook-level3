package seedu.addressbook.logic;

import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.commands.HelpCommand;
import seedu.addressbook.commands.RedoCommand;
import seedu.addressbook.commands.UndoCommand;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.parser.Parser;
import seedu.addressbook.state.ApplicationHistory;
import seedu.addressbook.state.ApplicationState;
import seedu.addressbook.storage.StorageFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents the main Logic of the AddressBook.
 */
public class Logic {


    private StorageFile storage;
    private AddressBook addressBook;
    private ApplicationHistory applicationHistory;

    /** The list of person shown to the user most recently.  */
    private List<? extends ReadOnlyPerson> lastShownList = Collections.emptyList();

    public Logic() throws Exception{
        setStorage(initializeStorage());
        setAddressBook(storage.load());
        setApplicationHistory(initializeApplicationHistory());
    }

    Logic(StorageFile storageFile, AddressBook addressBook){
        setStorage(storageFile);
        setAddressBook(addressBook);
        setApplicationHistory(initializeApplicationHistory());
    }

    void setStorage(StorageFile storage){
        this.storage = storage;
    }

    void setAddressBook(AddressBook addressBook){
        this.addressBook = addressBook;
    }
    
    void setApplicationHistory(ApplicationHistory applicationHistory) {
        this.applicationHistory = applicationHistory;
    }

    /**
     * Creates the StorageFile object based on the user specified path (if any) or the default storage path.
     * @throws StorageFile.InvalidStorageFilePathException if the target file path is incorrect.
     */
    private StorageFile initializeStorage() throws StorageFile.InvalidStorageFilePathException {
        return new StorageFile();
    }

    public String getStorageFilePath() {
        return storage.getPath();
    }

    /**
     * Unmodifiable view of the current last shown list.
     */
    public List<ReadOnlyPerson> getLastShownList() {
        return Collections.unmodifiableList(lastShownList);
    }

    protected void setLastShownList(List<? extends ReadOnlyPerson> newList) {
        lastShownList = newList;
    }

    /**
     * Creates a new instance of ApplicationHistory for managing the application state objects.
     */
    private ApplicationHistory initializeApplicationHistory() {
        return new ApplicationHistory();
    }
    
    /**
     * Parses the user command, executes it, and returns the result.
     * @throws Exception if there was any problem during command execution.
     */
    public CommandResult execute(String userCommandText) throws Exception {
        Command command = new Parser().parseCommand(userCommandText, applicationHistory);
        CommandResult result = execute(command);
        recordResult(result);
        return result;
    }

    /**
     * Executes the command, updates storage, and returns the result.
     *
     * @param command user command
     * @return result of the command
     * @throws Exception if there was any problem during command execution.
     */
    private CommandResult execute(Command command) throws Exception {
        applicationHistory.saveStateBeforeOperation(new ApplicationState(addressBook, lastShownList));
        command.setData(addressBook, lastShownList);
        CommandResult result = command.execute();
        storage.save(addressBook);
        updateApplicationHistory(command);
        return result;
    }

    /** Updates the {@link #lastShownList} if the result contains a list of Persons. */
    private void recordResult(CommandResult result) {
        final Optional<List<? extends ReadOnlyPerson>> personList = result.getRelevantPersons();
        if (personList.isPresent()) {
            lastShownList = personList.get();
        }
    }

    /**
     * Updates the application history using different logic depending on the command type.
     *
     * @param command the command that was just successfully executed.
     */
    private void updateApplicationHistory(Command command) {
        if (!(command instanceof RedoCommand)
                && !(command instanceof UndoCommand)
                && !(command instanceof HelpCommand)) {
            applicationHistory.updateStateAfterSuccessfulOperation();
        }
    }
}
