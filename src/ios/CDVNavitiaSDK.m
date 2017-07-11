#import "CDVNavitiaSDK.h"
#import <Cordova/CDV.h>

@implementation CDVNavitiaSDK

- (void)init:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* token = [command.arguments objectAtIndex:0];
    
    if ([token isKindOfClass:[NSNull class]] || [token length] == 0) {
        NSString* errorMessage = @"No token provided";
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
    } else {
        NavitiaConfiguration *conf = [[NavitiaConfiguration alloc] initWithToken:token];
        self.sdk = [[NavitiaSDK alloc] initWithConfiguration:conf];
        NSString* successMessage = @"SDK initialized with token";
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[NSString stringWithFormat:@"%@ %@", successMessage, token]];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)PlacesRequestBuilder_get:(CDVInvokedUrlCommand*)command
{
    if ([self.sdk isKindOfClass:[NSNull class]]) {
        CDVPluginResult* pluginResult = nil;
        NSString* errorMessage = @"NavitiaSDK is not instanciated";
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    NSDictionary* params = [command.arguments objectAtIndex:0];
    if ([params isKindOfClass:[NSNull class]] || [params count] == 0) {
        CDVPluginResult* pluginResult = nil;
        NSString* errorMessage = @"Problems with parameters handling";
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    PlacesRequestBuilder *placesRequestBuilder = [[self.sdk placesApi] newPlacesRequestBuilder];

    id value = nil;
    value = [params objectForKey:@"q"];
    if ([value isKindOfClass:[NSNumber class]]) {
        value = [value stringValue];
        [placesRequestBuilder withQ:value];
    }

    [placesRequestBuilder rawGetWithCompletion:^(NSString *results, NSError *error)
    {
        if ([error isKindOfClass:[NSNull class]]) {
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:results];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

        } else {
            NSLog(@"SDK places fail");
            NSDictionary *userInfo = @{
                @"NSLocalizedDescriptionKey" : [error localizedDescription]
            };
            NSError *error = [NSError errorWithDomain:@"NavitiaSDK" code:[error code] userInfo:userInfo];
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

        }
    }];

}

@end
