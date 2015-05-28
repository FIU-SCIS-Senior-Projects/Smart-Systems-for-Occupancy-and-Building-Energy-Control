<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}


$region_name = $_POST['region_name'];
$location = $_POST['location'];
$windows = $_POST['windows'];

$sql = "INSERT INTO zone_description(region_name, location, windows) VALUES ('".$region_name."', '".$location."', '".$windows."')";

$result = mysqli_query($conn, $sql);

$con = "";
$con .= "{ \"newzone_obj\": [";

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        $con .= " { \"region_name\": \"".$row["region_name"]."\", \"location\": ".$row["location"]." }, ";
    }
	$con .= "{ \"region_name\": \"null\", \"location\": 0 } ] }";
	echo $con;
} else {
    echo "No Data";
}
mysqli_close($conn);
?>