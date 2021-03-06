package BookManageSystem.controller;

import BookManageSystem.MainApp;
import BookManageSystem.beans.AdminBean;
import BookManageSystem.beans.BookBeanTableData;
import BookManageSystem.beans.BookTypeBean;
import BookManageSystem.beans.StateBean;
import BookManageSystem.dao.BookDao;
import BookManageSystem.dao.BookTypeDao;
import BookManageSystem.tools.SimpleTools;
import Client.ClientThread;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;

import java.util.List;

public class BookManageFrameController {

    @FXML
    private TextField bookNumberTextField;
    private SimpleTools simpleTools = new SimpleTools();
    private BookDao bookDao = new BookDao();
    private static final Logger logger=Logger.getLogger(BookManageFrameController.class);

    @FXML
    private TextField idTextField;

    @FXML
    private Button alterButton;

    @FXML
    private RadioButton maleRadioButton;

    @FXML
    private RadioButton femaleRadioButton;

    @FXML
    private TextField bookAuthorTextField2;

    @FXML
    private ComboBox bookTypeComboBox2;

    @FXML
    private TableColumn<BookBeanTableData, String> idTableColumn;

    @FXML
    private TableColumn<BookBeanTableData, String> authorSexTableColumn;

    @FXML
    private TableColumn<BookBeanTableData, String> bookPriceTableColumn;

    @FXML
    private TableColumn<BookBeanTableData,String> bookNumTableColumn;

    @FXML
    private ComboBox<?> bookTypeComboBox;

    @FXML
    private Button checkButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button resetButton2;

    @FXML
    private TableColumn<BookBeanTableData, String> bookAuthorTableColumn;

    @FXML
    private TableView<BookBeanTableData> bookManageTableView;

    @FXML
    private TextArea bookDescriptionTextArea;

    @FXML
    private TextField bookAuthorTextField;

    @FXML
    private TableColumn<BookBeanTableData, String> bookNameTableColumn;

    @FXML
    private TableColumn<BookBeanTableData, String> bookDescriptionTableColumn;

    @FXML
    private TextField bookNameTextField2;

    @FXML
    private TextField priceTextField;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField bookNameTextField;

    @FXML
    private TableColumn<BookBeanTableData, String> bookTypeTableColumn;
    private AdminBean admin;

    /**
     * ?????????????????????????????????
     */
    public void initialize() {
        // ???????????????????????????
        simpleTools.setLabeledImage(new Labeled[]{alterButton, deleteButton, resetButton2}, new String[]{"src/BookManageSystem/images/edit" +
                ".png",
                "src/BookManageSystem/images/delete.png", "src/BookManageSystem/images/reset.png"});
        // ????????????id???????????????????????????
        idTextField.setDisable(true);
        // ?????????????????????SQL??????
        String sql = "select bId,bBookName,bAuthor,bSex,bPrice,bBookDescription,btName,bNum from tb_book,tb_booktype where tb_book.btId=tb_booktype.btId;";
        // ??????????????????????????????????????????
        simpleTools.setBookTableViewData(bookManageTableView
                , simpleTools.getBookTableViewData(sql)
                , idTableColumn
                , bookNameTableColumn
                , bookAuthorTableColumn
                , authorSexTableColumn
                , bookPriceTableColumn
                , bookDescriptionTableColumn
                , bookTypeTableColumn
                ,bookNumTableColumn
        );

        // ?????????????????????SQL??????
        String getBookTypeSQL = "select * from tb_booktype";
        List bookTypeList = new BookTypeDao().getRecordsDataBySql(getBookTypeSQL);
        String[] typeNames = new String[bookTypeList.size()];
        for (int i = 0; i < bookTypeList.size(); i++) {
            BookTypeBean bookTypeBean = (BookTypeBean) bookTypeList.get(i);
            typeNames[i] = bookTypeBean.getBookTypeName();
        }
        // ??????????????????????????????
        simpleTools.addComboBoxItems(bookTypeComboBox, typeNames);
        simpleTools.addComboBoxItems(bookTypeComboBox2, typeNames);

        // ??????????????????????????????
        bookManageTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookDetails(newValue));
    }

    // ????????????????????????????????????
    public void do_checkButton_event(ActionEvent event) {
        // ??????SQL??????
        String sql = "select bId,bBookName,bAuthor,bSex,bPrice,bBookDescription,btName,bNum from tb_book,tb_booktype where" +
                " tb_book.btId=tb_booktype.btId ";
        // ???????????????????????????????????????????????????
        if (!simpleTools.isEmpty(bookNameTextField.getText())) {
            sql += " and bBookName like '%" + bookNameTextField.getText() + "%'";
        }
        // ???????????????????????????????????????????????????
        if (!simpleTools.isEmpty(bookAuthorTextField.getText())) {
            sql += " and bAuthor like '%" + bookAuthorTextField.getText() + "%'";
        }
        // ????????????????????????????????????
        String booktype = (String) bookTypeComboBox.getSelectionModel().selectedItemProperty().getValue();
        if (!simpleTools.isEmpty(booktype)) {
            sql += " and btName='" + booktype + "';";
        }
        // ??????SQL????????????????????????????????????????????????????????????????????????
        simpleTools.setBookTableViewData(bookManageTableView
                , simpleTools.getBookTableViewData(sql)
                , idTableColumn
                , bookNameTableColumn
                , bookAuthorTableColumn
                , authorSexTableColumn
                , bookPriceTableColumn
                , bookDescriptionTableColumn
                , bookTypeTableColumn
                ,bookNumTableColumn
        );
    }

    // ????????????????????????????????????
    public void do_alterButton_event(ActionEvent event) {
        // ?????????????????????????????????
        String id = idTextField.getText();
        String bookName = bookNameTextField2.getText();
        String authorSex = "";
        if (maleRadioButton.isSelected()) {
            authorSex = maleRadioButton.getText();
        } else if (femaleRadioButton.isSelected()) {
            authorSex = femaleRadioButton.getText();
        }
        String price = priceTextField.getText();
        String bookAuthor = bookAuthorTextField2.getText();
        String bookType = (String) bookTypeComboBox2.getSelectionModel().selectedItemProperty().getValue();
        String description = bookDescriptionTextArea.getText();
        // ??????SQL??????
        String bookTypeSQL = "select * from tb_booktype where btName='" + bookType + "';";
        List bookTypeList = new BookTypeDao().getRecordsDataBySql(bookTypeSQL);
        BookTypeBean bookTypeBean = (BookTypeBean) bookTypeList.get(0);
        // ??????????????????id
        int bookTypeId = bookTypeBean.getBookTypeId();
        // ????????????SQL??????
        String alterSQL =
                "update tb_book set bBookName='" + bookName + "',bAuthor='" + bookAuthor + "',bSex='" + authorSex +
                        "',bPrice=" + price + ",bBookDescription='" + description + "',btId=" + bookTypeId + " where " +
                        "bId=" + id + ";";
        // ??????SQL?????????????????????

        Thread thread=new Thread(new ClientThread(MainApp.getClient(),alterSQL,5));
        thread.start();
        try{
            thread.join();
        }catch (InterruptedException e){
            logger.error(e.getMessage());
        }

        //boolean isOK = bookDao.dataChange(alterSQL);
        // ?????????????????????
        if (StateBean.isOk1) {
            // ?????????????????????????????????????????????????????????
            initialize();
            do_resetButton2_event(null);
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "??????", "??????", "???????????????");
            logger.info("????????????"+admin.getAdmin_id()+"???????????????????????????");
        } else {
            // ??????????????????????????????
            simpleTools.informationDialog(Alert.AlertType.ERROR, "??????", "??????", "???????????????");
            logger.info("????????????"+admin.getAdmin_id()+"???????????????????????????");
        }
    }

    // ????????????????????????????????????
    public void do_deleteButton_event(ActionEvent event) {
        // ??????id???????????????
        String id = idTextField.getText();
        // ??????SQL???????????????id???????????????
        String deleteSQL = "delete from tb_book where bId=" + id + ";";
        // ??????????????????

        Thread thread=new Thread(new ClientThread(MainApp.getClient(),deleteSQL,5));
        thread.start();
        try{
            thread.join();
        }catch (InterruptedException e){
            logger.error(e.getMessage());
        }
        //boolean isOK = bookDao.dataChange(deleteSQL);
        // ???????????????????????????
        if (StateBean.isOk1) {
            // ???????????????????????????
            initialize();
            do_resetButton2_event(null);
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "??????", "??????", "???????????????");
            logger.info("????????????"+admin.getAdmin_id()+"?????????????????????");
        } else {
            // ???????????????????????????
            simpleTools.informationDialog(Alert.AlertType.ERROR, "??????", "??????", "???????????????");
            logger.info("????????????"+admin.getAdmin_id()+"?????????????????????");
        }
    }

    // ????????????????????????????????????
    public void do_resetButton2_event(ActionEvent event) {
        // ??????????????????
        simpleTools.clearTextField(idTextField, bookNameTextField2, priceTextField, bookAuthorTextField2,
                bookDescriptionTextArea);
        simpleTools.clearSelectedRadioButton(femaleRadioButton, maleRadioButton);
        simpleTools.clearSelectedComboBox(bookTypeComboBox2);
    }

    // ????????????????????????????????????
    public void do_resetButton_event(ActionEvent event) {
        // ???????????????????????????
        simpleTools.clearTextField(bookNameTextField, bookAuthorTextField);
        simpleTools.clearSelectedComboBox(bookTypeComboBox);
        initialize();
    }

    // ???????????????????????????????????????????????????
    public void showBookDetails(BookBeanTableData bookBeanTableData) {
        // ?????????????????????????????????????????????
        if (bookManageTableView.getSelectionModel().getSelectedIndex() < 0) {
            return;
        } else {
            // ??????????????????????????????????????????????????????????????????????????????????????????
            idTextField.setText(bookBeanTableData.getBookId());
            bookNameTextField2.setText(bookBeanTableData.getBookName());
            if (bookBeanTableData.getBookAuthorSex().equals("???")) {
                maleRadioButton.setSelected(true);
            } else if (bookBeanTableData.getBookAuthorSex().equals("???")) {
                femaleRadioButton.setSelected(true);
            }
            priceTextField.setText(bookBeanTableData.getBookPrice());
            bookAuthorTextField2.setText(bookBeanTableData.getBookAuthor());
            // ????????????
            String str = bookBeanTableData.getBookType();
            int index = 0;
            List inputList = FXCollections.observableArrayList(bookTypeComboBox.getItems());
            for (int i = 0; i < inputList.size(); i++) {
                if (str.equals(inputList.get(i))) {
                    index = i;
                }
            }
            bookTypeComboBox2.getSelectionModel().select(index);
            bookDescriptionTextArea.setText(bookBeanTableData.getBookDescription());
            bookNumberTextField.setText(bookBeanTableData.getBookNum());
        }
    }

    public void setAdmin(AdminBean adminBean) {
        this.admin=adminBean;
    }
}
