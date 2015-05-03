<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}


$name = $_POST['name'];
$password = $_POST['password'];
$login_email = $_POST['login_email'];

$sql = "INSERT INTO user(name, password, login_email) VALUES ('".$name."', '".$password."', '".$login_email."')";

$result = mysqli_query($conn, $sql);

$con = "";
$con .= "{ \"newuser_obj\": [";

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        $con .= " { \"name\": \"".$row["name"]."\", \"login_email\": ".$row["login_email"]." }, ";
    }
	$con .= "{ \"name\": \"null\", \"login_email\": 0 } ] }";
	echo $con;
} else {
    echo "No Data";
}
mysqli_close($conn);
?>