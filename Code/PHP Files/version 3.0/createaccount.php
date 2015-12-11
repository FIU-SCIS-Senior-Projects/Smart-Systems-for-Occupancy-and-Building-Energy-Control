<?php
//Import our variables to connect to the database
require_once__DIR__. '/variables.php';

//Create connection
$conn = mysqli_connect("localhost","root","hidalgopresa","ssobec_db");

//Check connection
if(!$conn){
die("Connection failed: ".mysqli_connect_error());
}

$name=$_POST['name'];
$password=$_POST['password'];
$login_email=$_POST['login_email'];
$sql="INSERT INTO user (name,password,login_email,user_type) VALUES ('".$name."','".$password."','".$login_email."','user')";
$result = mysqli_query($conn,$sql);
$con = "";
$con .= "{\"newuser_obj\":[";

if(mysqli_num_rows($result)>0){
//output data of each row
	while($row=mysqli_fetch_assoc($result)){
		$con.="{\"name\": \"".$row["name"]."\", \"login_email\": \"".$row["login_email"]."\"},";
	}
	$con .= "{\"name\": \"null\",\"login_email\": \"null\"}]}";
	echo $con;
}else{
	echo "No Data";
}
mysqli_close($conn);
?>
