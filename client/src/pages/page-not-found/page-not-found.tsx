import ButtonLink from '@/components/custom/button-link/button-link';
import ErrorNotFound from '@/components/custom/error/error-not-found';
import { Box, Flex, Text } from '@chakra-ui/react';

export const PageNotFound = () => {
  return (
    <Box
      bg="background"
      w="100vw"
      h="100vh"
      display="flex"
      alignItems="center"
      justifyContent="center"
      p={5}
      margin="auto"
    >
      <Flex
        direction="column"
        align="center"
        justify="center"
        w="100%"
        h="100%"
        margin="auto"
        mt={-8}
      >
        <ErrorNotFound />
        <Text
          color="text"
          fontSize={{ base: 'sm', sm: 'md', md: 'xl' }}
          py={3}
          maxW={{ base: '300px', sm: '400px', md: '500px' }}
          display="block"
          textAlign="center"
        >
          <Text as="span" display="inline" color="primary">
            Oops!
          </Text>{' '}
          We couldn't find the page you're trying to reach.
        </Text>
        <ButtonLink text="Return to the homepage" href="/" />
      </Flex>
    </Box>
  );
};
