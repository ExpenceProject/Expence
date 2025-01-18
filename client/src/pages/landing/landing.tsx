import ButtonLink from '@/components/custom/button-link/button-link';
import { MoneyIcon } from '@/components/icons/money';
import { PageLayout } from '@/layout/page-layout';
import { openLoginModalAtom } from '@/utils/atoms/modal-atoms';
import { useUser } from '@/utils/providers/user-provider/use-user';
import {
  Button,
  Flex,
  Heading,
  Text,
  useBreakpointValue,
} from '@chakra-ui/react';
import { useAtom } from 'jotai';

export const LandingPage = () => {
  const [, openLoginModal] = useAtom(openLoginModalAtom);
  const { user } = useUser();
  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

  return (
    <PageLayout direction="row">
      <Flex direction="column" zIndex={1} width={{ lgDown: '100%', md: '65%' }}>
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
        {user ? (
          <ButtonLink text="Get started" href="/profile/groups" />
        ) : (
          <Button
            size={{ base: 'xs', sm: 'sm', md: 'xl' }}
            fontSize={{ base: 'sm', sm: 'md', md: 'lg' }}
            bg="primary"
            mt={4}
            w="min-content"
            mb={4}
            _hover={{ bg: 'hoverPrimary' }}
            style={{
              padding: `20px 20px`,
            }}
            onClick={openLoginModal}
            color="textRaw"
          >
            Sign In
          </Button>
        )}
      </Flex>
      {(currentBreakpoint === 'xl' || currentBreakpoint === 'lg') && (
        <Flex align="center" justify="center" width="35%">
          <MoneyIcon width={330} height={330} />
        </Flex>
      )}
    </PageLayout>
  );
};
