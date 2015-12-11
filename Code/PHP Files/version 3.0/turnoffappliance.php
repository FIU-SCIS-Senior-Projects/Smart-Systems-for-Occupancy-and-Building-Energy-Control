<?php

// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect("localhost","root","hidalgopresa", "ssobec_db");
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$userid = $_POST['user_id'];
$rewardstotal = $_POST['total_rewards'];
$points = $_POST['reward'];
$description = $POST['reward_description'];


$sql = "INSERT INTO user_rewards (user_id, description, points) VALUES ('".$userid."', 'Turned Off Appliance', '".$points."')";
$result = mysqli_query($conn, $sql);


$sql2 = "UPDATE user SET rewards_total = '".$rewardstotal."' WHERE user_id = '".$userid."'";
$res2 = mysqli_query($conn, $sql2);
if ($res2){
    echo 'Successful';
}
else {
    echo 'MySQL Error: '. mysqli_error($conn);
}
mysqli_close($conn);
?>
