<?php


// Import our variables to connect to the database
require_once __DIR__ . '/variables.php';

// Create connection
$conn = mysqli_connect(hostname_db,username_db,password_db, database_db);
// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$email = $_POST['login_email'];
$password = $_POST['password'];
$sql = "select * from user where login_email = '" . $email . "' AND password = '" . $password . "'";
$result = mysqli_query($conn, $sql);


if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        echo "id:" . $row["user_id"]. ":name:" . $row["name"]. ":email:" . $row["login_email"]. ":usertype:" .$row["user_type"]. ":rewards:" .$row["rewards_total"]. "";
    }
} else {
    echo "Not A User2, email: ".$email." pass: ".$password."";
}
mysqli_close($conn);
?>
