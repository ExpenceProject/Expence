import Money from '@/components/icons/money/money';
import {
  coreMobilePaddingX,
  corePaddingX,
  maxWebsiteWidth,
} from '@/style/variables';
import {
  Box,
  Button,
  Flex,
  Heading,
  Text,
  useBreakpointValue,
} from '@chakra-ui/react';
import { HiMiniArrowRight } from 'react-icons/hi2';

export const LandingPage = () => {
  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

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
      <Flex w="100%" maxW={maxWebsiteWidth}>
        <Flex
          direction="column"
          zIndex={1}
          width={{ lgDown: '100%', md: '65%' }}
        >
          <Heading
            color="textRaw"
            fontSize={{ base: '4xl', md: '5xl' }}
            py={5}
            lineHeight={1.2}
            letterSpacing="-0.02em"
            wordBreak="normal"
            maxW={{ base: '400px', sm: '500px', md: '600px' }}
          >
            Effortless expense sharing{' '}
            {currentBreakpoint && currentBreakpoint !== 'sm' && <br />} for any
            group gathering.
          </Heading>
          <Text
            color="text"
            fontSize={{ base: 'sm', sm: 'md', md: 'xl' }}
            py={3}
            maxW={{ base: '300px', sm: '400px', md: '520px' }}
            display="block"
          >
            From dinners to trips,
            <Text as="span" display="inline" color="primary">
              {' '}
              Expence{' '}
            </Text>
            makes it easy to keep track of who paid what. No more messy
            calculations or awkward reminders.
          </Text>
          <Button
            bg="primary"
            color="textBg"
            px={8}
            mt={8}
            w="min-content"
            size={{ base: 'xs', sm: 'sm', md: 'xl' }}
            display="flex"
            gap={1}
            _hover={{ bg: 'hover' }}
          >
            Get started <HiMiniArrowRight />
          </Button>
        </Flex>
        {(currentBreakpoint === 'xl' || currentBreakpoint === 'lg') && (
          <Flex align="center" justify="center" width="35%">
            <Money width={330} height={330} />
          </Flex>
        )}
      </Flex>
    </Box>
  );
};
