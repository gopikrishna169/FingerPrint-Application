import React from 'react';
import { View, Text, Button, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const Landing = () => {
const navigation = useNavigation();

  return (
    <>
      <View style={styles.container}>
        <Text style={styles.title}>Welcome to the Gym !</Text>
      </View>
      <Button
        title="Register Fingerprint"
        onPress={() => navigation.navigate('Register')}
        style={styles.button}
      />
      <View style={styles.space} />
      <Button
        title="Mark Attendance"
        onPress={() => navigation.navigate('Attendance')}
        style={styles.button}
      />
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    marginTop: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 20,
    marginBottom: 30,
  },
  button: {
    marginTop: 30,
    width: '100%',
  },
  space: {
    height: 20,
  }
});

export default Landing;
