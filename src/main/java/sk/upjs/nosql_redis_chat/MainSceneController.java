package sk.upjs.nosql_redis_chat;

import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainSceneController {

    @FXML private ListView<String> chatAreaListView;
    @FXML private Button sendButton;
    @FXML private TextField textToSendTextField;
    @FXML private TextField menoTextField;
    private RedisTemplate<String,String> redisTemplate;
    private RedisConnection redisConnection;
    public static final String CHANNEL_NAME = "chat2020";

	private ObservableList<String> spravy;

	public MainSceneController(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(RedisConfiguration.class);
		context.refresh();
		redisTemplate = context.getBean(RedisTemplate.class);
		redisConnection = context.getBean(RedisConnection.class);
	}

    @FXML
    void initialize() {
    	spravy = FXCollections.observableArrayList(new ArrayList<String>());
    	chatAreaListView.setItems(spravy);
    	String meno = menoTextField.textProperty().getValue();
    	if (meno == null || meno.trim().length() == 0) {
    		meno = "user" + (int)(100000*Math.random());
    		menoTextField.textProperty().setValue(meno);
    	}
    	sendButton.setDefaultButton(true);
    	sendButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String text = textToSendTextField.textProperty().getValue();
				if (text != null && text.trim().length() > 0) {
					String name = menoTextField.textProperty().getValue();
          			redisTemplate.convertAndSend(CHANNEL_NAME,name +": "+text );
				}
			}
		});
    	SubscriberService service = new SubscriberService(redisConnection,spravy);
		service.start();
    }
}
