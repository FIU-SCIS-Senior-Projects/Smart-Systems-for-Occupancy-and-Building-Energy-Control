<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect("localhost","root","hidalgopresa", "ssobec_db");
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$region_name = $_POST['region_name'];
$location = $_POST['location'];
$windows = $_POST['windows'];

$sql = "INSERT INTO zone_description (region_name, location, windows) VALUES ('".$region_name."', '".$location."', '".$windows."')";

$result = mysqli_query($conn, $sql);

//check if INSERT was successful
if ($result) {
    
    echo 'Successful';
} else {
    echo 'MySQL Error: '. mysqli_error($conn);
}
mysqli_close($conn);
?>
