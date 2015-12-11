<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$mysql = $_POST['sql'];
$sql = "".$mysql."";

$result = mysqli_query($conn, $sql);

if($result) // Check if insert statement was successful
{
    //Show success message
    echo 'successfully inserted data in record';
}else{
    //Show error message
    echo 'Mysql Error: '. mysqli_error($conn);
}


mysqli_close($conn);
?>
