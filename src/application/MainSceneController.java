//Pavitra Patel (php51), Huzaif Mansuri (htm23)

package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainSceneController
{
	static File inputFile;
	static Scanner sc;
	
	static 
	{
		try
		{
			inputFile = new File("input.txt");
			inputFile.createNewFile();
			sc = new Scanner(inputFile);
		}
		catch (IOException e) 
		{
		     throw new ExceptionInInitializerError(e.getMessage());
	    }
	}
	@FXML
	private TextField album;

	@FXML
	private TextField artist;

	@FXML
	private TextField name;

	@FXML
	private ListView<String> songlist;
	
	private ObservableList<String> songs;
	
	@FXML
	private TextField year;

	@FXML
	private VBox display;

	public void start() throws IOException
	{
		songs = FXCollections.observableArrayList();
		songlist.setItems(songs);
		while(sc.hasNext()) 
		{
			String song = sc.nextLine();
			songs.add(song);
		}
		if(!songs.isEmpty())
		{
			songlist.getSelectionModel().select(0);
		}
		sc.close();
		displaysong();
		songlist.setCellFactory(new Callback<ListView<String>, ListCell<String>>()
		{
			@Override
			public ListCell<String> call(ListView<String> arg0) 
			{
				return new ListCell<String>()
				{
					protected void updateItem(String item, boolean empty)
					{
						super.updateItem(item, empty);
						if(!empty && item != null)
						{
							int firstIndex = item.indexOf("|");
							int secondIndex = item.indexOf("|", firstIndex + 1);
							Label l = new Label(item.substring(0, secondIndex));
							setGraphic(l);
						}
						else
						{
							setGraphic(null);
						}
					}
				};
			}
		});
	}

	public ObservableList<String> storeData()
	{
		return songs;
	}
	
	@FXML
	public void addsong(MouseEvent event) throws IOException
	{
		String element = name.getText().trim()+" | "+artist.getText().trim()+" | "+album.getText().trim()+" | "+year.getText().trim();
		boolean error = false;
		try
		{
			if(year.getText() != "")
			{
				int tempYear = Integer.parseInt(year.getText());
				if(tempYear <= 0)
					throw new Exception();
			}
		}
		catch(Exception e)
		{
			error = true;
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("ALERT!!!");
			alert.setHeaderText(null);
			alert.setContentText("Year should be a positive integer!");
			alert.showAndWait();
		}
		if(!error && (name.getText().contains("|") || artist.getText().contains("|") || album.getText().contains("|") || name.getText().contains("|")))
		{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ALERT!!!");
			alert.setHeaderText(null);
			alert.setContentText("Special character '|' not allowed.");
			alert.showAndWait();
		}
		else if(!error && !name.getText().trim().isEmpty() && !artist.getText().trim().isEmpty())
		{
			int first = element.indexOf("|");
			int second = element.indexOf("|", first+1);
			String check = element.substring(0, second);
			Predicate<String> duplicate = (String s) -> (s.substring(0, s.indexOf("|",s.indexOf("|")+1)).trim().equalsIgnoreCase(check.trim()));
			if(!songs.stream().anyMatch(duplicate))
			{
				Alert confirm = new Alert(Alert.AlertType.WARNING);
				confirm.setTitle("WARNING!!!");
				confirm.setContentText("Are you sure you want to add entered song details?");
				confirm.setHeaderText(null);
				confirm.setResizable(false);
				confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
				Optional<ButtonType> result = confirm.showAndWait();
				if(result.get() == ButtonType.OK)
				{
					songs.add(element);
					FXCollections.sort(songs, String.CASE_INSENSITIVE_ORDER);
					int index = songs.indexOf(element);
					songlist.getSelectionModel().select(index);
					displaysong();
				}
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("ALERT!!!");
				alert.setHeaderText(null);
				alert.setContentText("The song detail that you are trying to enter already exists.");
				alert.showAndWait();
			}
		}
		else if(!error)
		{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ALERT!!!");
			alert.setHeaderText(null);
			alert.setContentText("To add a song the minimum combination of the name and the artist is must.");
			alert.showAndWait();
		}
		name.clear();
		album.clear();
		artist.clear();
		year.clear();
		error = false;
	}
	
	@FXML
	public void deletesong(MouseEvent event)
	{
		int selectedID = songlist.getSelectionModel().getSelectedIndex();
		if(selectedID != -1)
		{
			Alert confirm = new Alert(Alert.AlertType.WARNING);
			confirm.setTitle("WARNING!!!");
			confirm.setContentText("Are you sure you want to delete selected song details?");
			confirm.setHeaderText(null);
			confirm.setResizable(false);
			confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
			Optional<ButtonType> result = confirm.showAndWait();
			if(result.get() == ButtonType.OK)
			{
				songs.remove(selectedID);
				if(selectedID>=songs.size())
					selectedID -= 1;
				if(!songs.isEmpty())
				{
					songlist.getSelectionModel().select(selectedID);
				}
			}
			displaysong();
		}
	}
	
	@FXML
	public void editsong(MouseEvent event)
	{
		int selectedID = songlist.getSelectionModel().getSelectedIndex();
		String element = name.getText().trim()+" | "+artist.getText().trim()+" | "+album.getText().trim()+" | "+year.getText().trim();
		
		boolean error = false;
		try
		{
			if(year.getText() != "")
			{
				int tempYear = Integer.parseInt(year.getText());
				if(tempYear <= 0)
					throw new Exception();
			}
		}
		catch(Exception e)
		{
			error = true;
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("ALERT!!!");
			alert.setHeaderText(null);
			alert.setContentText("Year should be a positive integer!");
			alert.showAndWait();
		}
		
		if(!error && (name.getText().contains("|") || artist.getText().contains("|") || album.getText().contains("|") || name.getText().contains("|")))
		{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ALERT!!!");
			alert.setHeaderText(null);
			alert.setContentText("Special character '|' not allowed.");
			alert.showAndWait();
		}
		else if(!error && selectedID != -1 && !name.getText().isEmpty() && !artist.getText().isEmpty())
		{
			int first = element.indexOf("|");
			int second = element.indexOf("|", first+1);
			String check = element.substring(0, second);
			
			String tempCheck = songlist.getSelectionModel().getSelectedItem();
			int tempFirst = element.indexOf("|");
			int tempSecond = element.indexOf("|", tempFirst+1);
			tempCheck = tempCheck.substring(0, tempSecond);
			
			Predicate<String> duplicate = (String s) -> (s.substring(0, s.indexOf("|",s.indexOf("|")+1)).trim().equalsIgnoreCase(check.trim()));
			if(tempCheck.equals(check) || !songs.stream().anyMatch(duplicate))
			{
				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
				confirm.setTitle("CONFIRMATION REQUIRED!!!");
				confirm.setContentText("Are you sure you want to edit selected song details with new entered data?");
				confirm.setHeaderText(null);
				confirm.setResizable(false);
				confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
				Optional<ButtonType> result = confirm.showAndWait();
				if(result.get() == ButtonType.OK)
				{
					songs.set(selectedID, element);
					FXCollections.sort(songs, String.CASE_INSENSITIVE_ORDER);
					int index = songs.indexOf(element);
					songlist.getSelectionModel().select(index);
					displaysong();
				}
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("ALERT!!!");
				alert.setHeaderText(null);
				alert.setContentText("The song detail that you are trying to enter already exists.");
				alert.showAndWait();
			}
		}
		else if(!error)
		{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ALERT!!!");
			alert.setHeaderText(null);
			alert.setContentText("To edit a song the minimum combination of the name and the artist is must.");
			alert.showAndWait();
		}
		name.clear();
		album.clear();
		artist.clear();
		year.clear();
		displaysong();
		error = false;
	}
	
	@FXML
	public void displaysong()
	{	
		Label label0 = new Label("Selected Song Details: ");
		Label label1 = new Label("Name: ");
	    Label label2 = new Label("Artist: ");
	    Label label3 = new Label("Album: ");
	    Label label4 = new Label("Year: ");
	    if(!songs.isEmpty()) 
	    {
	    	
			int selectedID = songlist.getSelectionModel().getSelectedIndex();
			if(selectedID == -1)
				selectedID = 0;
			StringBuilder element = new StringBuilder(songs.get(selectedID));
			
			int firstIndex = element.indexOf("|");
			int secondIndex = element.indexOf("|", firstIndex + 1);
			int thirdIndex = element.indexOf("|", secondIndex + 1);
			
			String first = element.substring(0, firstIndex);
			String second = element.substring(firstIndex + 1, secondIndex);
			String third = element.substring(secondIndex + 1, thirdIndex);
			String fourth = element.substring(thirdIndex + 1);
			fourth = fourth.substring(0, fourth.length());
			
			label0.setText("Selected Song Details: ");
			label1.setText("Name: " + first);
		    label2.setText("Artist: " + second);
		    label3.setText("Album: " + third);
		    label4.setText("Year: " + fourth);
	    }
	    display.getChildren().clear();
	    display.setSpacing(10);
	    display.getChildren().addAll(label0, label1, label2, label3, label4);
	}
}