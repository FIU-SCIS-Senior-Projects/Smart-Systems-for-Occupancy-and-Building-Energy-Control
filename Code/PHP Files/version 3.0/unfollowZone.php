<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$region_id = $_POST['region_id'];
$user_id = $_POST['user_id'];

$sql = "DELETE FROM region_authority WHERE zone_description_region_id = '" . $region_id . "' AND user_user_id = '" . $user_id . "';";

$result = mysqli_query($conn, $sql);

if($result) // Check if insert statement was successful
{
    //Show success message
    echo 'Successful';
}else{
    //Show error message
    echo 'Mysql Error: '. mysqli_error($conn);
}


mysqli_close($conn);
?>
