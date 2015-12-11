<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$login_email = $_POST['login_email'];
$region_id = $_POST['region_id'];

$sql = "SELECT * FROM user WHERE login_email ='" . $login_email. "';";

$result = mysqli_query($conn, $sql);

$user_id = null;

if($result) {
    //echo "Succesful 1";
    if(mysqli_num_rows($result) > 0){ 
        //echo "Number of Rows > 0";
    
        while ($row = mysqli_fetch_assoc($result)) {
            $user_id = $row["user_id"];
        }

        $sql = "INSERT INTO region_authority(user_user_id, zone_description_region_id) VALUES (".$user_id.",".$region_id.")";

        $result = mysqli_query($conn, $sql);

        if($result) {
            echo 'Successful';
        }
        else {
            echo 'Mysql Error 2: '. mysqli_error($conn);
        }
    }
}
else {
    echo 'Mysql Error 1: '. mysqli_error($conn);
}

mysqli_close($conn);
?>
