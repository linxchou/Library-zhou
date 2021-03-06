package BookManageSystem.controller;

import BookManageSystem.MainApp;
import BookManageSystem.beans.*;
import BookManageSystem.dao.BookDao;
import BookManageSystem.dao.BookTypeDao;
import BookManageSystem.dao.ReaderDao;
import BookManageSystem.dao.l_rDao;
import BookManageSystem.tools.SimpleTools;
import Client.ClientThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.Socket;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class BookBorrowFrame {

    public static boolean isOk1;

    private static final Logger logger=Logger.getLogger(BookBorrowFrame.class);
    @FXML
    public TextField bookNameTextField;
    @FXML
    public TextField bookAuthorTextField;
    @FXML
    public ComboBox bookTypeComBox;
    @FXML
    public Button ckeckButton;
    @FXML
    public Button resetButton;
    @FXML
    private TableView<BookBeanTableData> bookManageTableView;

    @FXML
    public TextField bookNameTextField2;
    @FXML
    public TextField bookAuthorTextField2;
    @FXML
    public RadioButton maleRadioButton;
    @FXML
    public RadioButton femaleRadioButton;
    @FXML
    public ComboBox bookTypeComboBox2;
    @FXML
    public TextArea bookDescriptionTextArea1;
    @FXML
    public TextField bookNumber;
    @FXML
    public Button borrowButton;
    @FXML
    public Button resetButton2;
    @FXML
    public TextField idTextField;
    @FXML
    public TextField priceTextField;
    @FXML
    private TableColumn<BookBeanTableData, String> idTableColumn;
    @FXML
    private TableColumn<BookBeanTableData, String> bookNameTableColumn;

    @FXML
    private TableColumn<BookBeanTableData, String> authorSexTableColumn;

    @FXML
    private TableColumn<BookBeanTableData, String> bookPriceTableColumn;
    @FXML
    private TableColumn<BookBeanTableData, String> bookDescriptionTableColumn;
    @FXML
    private TableColumn<BookBeanTableData, String> bookTypeTableColumn;
    @FXML
    private TableColumn<BookBeanTableData, String> bookNumTableColumn;
    @FXML
    private TableColumn<BookBeanTableData, String> bookAuthorTableColumn;
    @FXML
    private Stage stage;
    public BookBean bookBean=new BookBean();






    public ReaderBean readerBean;

    private SimpleTools simpleTools = new SimpleTools();


    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }


    public void initialize() {
        // ???????????????????????????
        simpleTools.setLabeledImage(new Labeled[]{borrowButton, resetButton2}, new String[]{"src/BookManageSystem/images/edit" +
                ".png",
                "src/BookManageSystem/images/reset.png"});
        // ????????????id???????????????????????????
        idTextField.setDisable(true);
        bookNameTextField2.setDisable(true);
        priceTextField.setDisable(true);
        bookAuthorTextField2.setDisable(true);
        maleRadioButton.setDisable(true);
        femaleRadioButton.setDisable(true);
        bookTypeComboBox2.setDisable(true);
        //bookDescriptionTextArea1.setDisable(true);
        bookNumber.setDisable(true);

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
                , bookNumTableColumn
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
        simpleTools.addComboBoxItems(bookTypeComBox, typeNames);
        simpleTools.addComboBoxItems(bookTypeComboBox2, typeNames);

        // ??????????????????????????????
        bookManageTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookDetails(newValue));


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    ObservableList<BookBeanTableData>data=ReFreshTableView();
                    if(!simpleTools.isEmpty(idTextField.getText())){
                        for (BookBeanTableData datum : data) {
                            if (datum.getBookId().equals(idTextField.getText())) {
                                bookNumber.setText(datum.getBookNum());
                            }
                        }
                    }
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        logger.error(e.getMessage());
                    }

                }
            }
        }).start();
    }



    public ObservableList<BookBeanTableData> ReFreshTableView(){
        String sql = "select bId,bBookName,bAuthor,bSex,bPrice,bBookDescription,btName,bNum from tb_book,tb_booktype where tb_book.btId=tb_booktype.btId;";
        ObservableList<BookBeanTableData> data=simpleTools.getBookTableViewData(sql);
        simpleTools.setBookTableViewData(bookManageTableView
                , data
                , idTableColumn
                , bookNameTableColumn
                , bookAuthorTableColumn
                , authorSexTableColumn
                , bookPriceTableColumn
                , bookDescriptionTableColumn
                , bookTypeTableColumn
                , bookNumTableColumn
        );
        return data;

        //bookBean.setBookNum(Integer.parseInt(bookNumTableColumn.getText()));

    }
    public void ReFreshTableView(BookBean bookBean) {
        String sql = "select bId,bBookName,bAuthor,bSex,bPrice,bBookDescription,btName,bNum from tb_book,tb_booktype where tb_book.btId=tb_booktype.btId;";
        simpleTools.setBookTableViewData(bookManageTableView
                , simpleTools.getBookTableViewData(sql)
                , idTableColumn
                , bookNameTableColumn
                , bookAuthorTableColumn
                , authorSexTableColumn
                , bookPriceTableColumn
                , bookDescriptionTableColumn
                , bookTypeTableColumn
                , bookNumTableColumn
        );
        bookNumber.setText(String.valueOf(bookBean.getBookNum()));

    }

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
            List inputList = FXCollections.observableArrayList(bookTypeComBox.getItems());
            for (int i = 0; i < inputList.size(); i++) {
                if (str.equals(inputList.get(i))) {
                    index = i;
                }
            }
            bookTypeComboBox2.getSelectionModel().select(index);
            bookDescriptionTextArea1.setText(bookBeanTableData.getBookDescription());
            bookNumber.setText(bookBeanTableData.getBookNum());
        }
    }

    public void do_checkButton_event(ActionEvent actionEvent) {
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
        String booktype = (String) bookTypeComBox.getSelectionModel().selectedItemProperty().getValue();
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
                , bookNumTableColumn
        );

    }

    public void do_resetButton1_event(ActionEvent actionEvent) {
        // ???????????????????????????
        simpleTools.clearTextField(bookNameTextField, bookAuthorTextField);
        simpleTools.clearSelectedComboBox(bookTypeComBox);

    }

    public void do_borrowButton_event(ActionEvent actionEvent) {
        LendList lendList = new LendList();
        if (simpleTools.isEmpty(idTextField.getText())) {
            simpleTools.informationDialog(Alert.AlertType.ERROR, "??????", "??????", "???????????????????????????");
        }
        BookBean bookBean=new BookBean();
        bookBean.setBookId(Integer.parseInt(idTextField.getText()));
        bookBean.setBookNum(Integer.parseInt(bookNumber.getText()));

        if(bookBean.getBookNum()==0){
            simpleTools.informationDialog(Alert.AlertType.INFORMATION, "??????", "??????", "??????????????????");
        }
        else{
            lendList.setBook_id(Integer.parseInt(idTextField.getText()));
            lendList.setReader_id(readerBean.getReader_id());
            lendList.setName(readerBean.getName());
            lendList.setBookname(bookNameTextField2.getText());
            Calendar c = Calendar.getInstance();
            Date LendDate = new Date(c.getTimeInMillis());
            lendList.setLend_date(LendDate);
            c.add(Calendar.DATE, 60);
            Date LimitedDate = new Date(c.getTimeInMillis());
            lendList.setLimited_date(LimitedDate);


            Socket client = MainApp.getClient();
            Thread thread = new Thread(new ClientThread(client, lendList, 1));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            if (StateBean.isOk1) {
                bookBean.setBookNum(bookBean.getBookNum() - 1);
                simpleTools.informationDialog(Alert.AlertType.INFORMATION, "??????", "??????", "????????????!");
                logger.info("?????????"+readerBean.getReader_id()+"?????????"+lendList.getBookname()+"?????????");
                readerBean.setNum(readerBean.getNum() + 1);
                Thread thread1 = new Thread(new ClientThread(client, readerBean, 2));
                thread1.start();
                try {
                    thread1.join();

                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }

                if (StateBean.isOk2) {
                    logger.info("?????????????????????????????????");

                } else {
                    logger.error("?????????????????????????????????");
                }

                Thread thread2 = new Thread(new ClientThread(client, bookBean, 3));
                thread2.start();
                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }

                if (StateBean.isOk3) {
                    logger.info("???????????????????????????");
                } else {
                    logger.info("???????????????????????????");
                }


            } else{
                simpleTools.informationDialog(Alert.AlertType.ERROR, "??????", "??????", "????????????????????????");
                logger.info("????????????"+lendList.getBookname()+"???");
            }

            ReFreshTableView(bookBean);
        }



    }

    public void do_resetButton2_event(ActionEvent actionEvent) {
        // ??????????????????
        simpleTools.clearTextField(idTextField, bookNameTextField2, priceTextField, bookAuthorTextField2,
                bookDescriptionTextArea1);
        simpleTools.clearSelectedRadioButton(femaleRadioButton, maleRadioButton);
        simpleTools.clearSelectedComboBox(bookTypeComboBox2);
    }

    public void setReaderBean(ReaderBean readerBean) {
        this.readerBean = readerBean;
    }
}
