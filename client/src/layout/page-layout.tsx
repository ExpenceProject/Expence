import {
  coreMobilePaddingX,
  corePaddingX,
  maxWebsiteWidth,
} from '@/style/variables';
import { Flex } from '@chakra-ui/react';
import { FC, ReactNode } from 'react';

type PageLayoutProps = {
  children: ReactNode;
  direction?: 'row' | 'column';
  includeGap?: boolean;
};

export const PageLayout: FC<PageLayoutProps> = ({
  children,
  direction = 'column',
  includeGap = true,
}) => {
  return (
    <Flex
      bg="background"
      h="100%"
      w="100%"
      justifyContent="center"
      paddingX={{ base: coreMobilePaddingX, md: corePaddingX }}
      paddingTop={10}
    >
      <Flex
        bg="background"
        w="100%"
        maxW={maxWebsiteWidth}
        direction={
          direction === 'row' ? { base: 'column', md: 'row' } : 'column'
        }
        alignItems="center"
        justifyContent="center"
        gap={{ base: includeGap ? 10 : 0, md: includeGap ? 20 : 0 }}
      >
        {children}
      </Flex>
    </Flex>
  );
};
