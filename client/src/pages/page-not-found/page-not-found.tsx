import ButtonLink from '@/components/custom/button-link/button-link';
import ErrorNotFound from '@/components/custom/error/error-not-found';
import { coreMobilePaddingX, corePaddingX } from '@/style/variables';
import { Box, Flex, Text } from '@chakra-ui/react';

export const PageNotFound = () => {
  return (
    <Box
      bg="background"
      w="100%"
      display="flex"
      alignItems="center"
      justifyContent="center"
      px={{ base: coreMobilePaddingX, md: corePaddingX }}
      pt={20}
    >
      <Flex direction="column" alignItems="center" width={{ lgDown: '100%' }}>
        <ErrorNotFound />
        <Text
          color="text"
          fontSize={{ base: 'sm', sm: 'md', md: 'xl' }}
          py={3}
          maxW={{ base: '300px', sm: '400px', md: '500px' }}
          display="block"
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
